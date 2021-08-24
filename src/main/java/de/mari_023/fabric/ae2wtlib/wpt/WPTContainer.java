package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.ICraftingHelper;
import appeng.api.definitions.IDefinitions;
import appeng.api.networking.IGridNode;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.ContainerLocator;
import appeng.container.ContainerNull;
import appeng.container.SlotSemantic;
import appeng.container.guisync.GuiSync;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.container.me.items.ItemTerminalContainer;
import appeng.container.slot.*;
import appeng.core.Api;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.packets.PatternSlotPacket;
import appeng.helpers.IContainerCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.me.helpers.MachineSource;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.AdaptorFixedInv;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.inv.WrapperCursorItemHandler;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.mixin.ScreenHandlerMixin;
import de.mari_023.fabric.ae2wtlib.mixin.SlotMixin;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.util.ContainerHelper;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;

public class WPTContainer extends ItemTerminalContainer implements IAEAppEngInventory, IOptionalSlotHost, IContainerCraftingPacket, IWTInvHolder {

    public static ScreenHandlerType<WPTContainer> TYPE;

    public static final ContainerHelper<WPTContainer, WPTGuiObject> helper = new ContainerHelper<>(WPTContainer::new, WPTGuiObject.class);

    public static WPTContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    private final FixedItemInv craftingGridInv;
    private final FakeCraftingMatrixSlot[] craftingGridSlots = new FakeCraftingMatrixSlot[9];
    private final OptionalFakeSlot[] processingOutputSlots = new OptionalFakeSlot[3];
    private final PatternTermSlot craftOutputSlot;
    private final RestrictedInputSlot blankPatternSlot;
    private final RestrictedInputSlot encodedPatternSlot;
    private CraftingRecipe currentRecipe;
    private final ICraftingHelper craftingHelper = Api.INSTANCE.crafting();

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WPTGuiObject wptGUIObject;

    @GuiSync(97)
    public boolean craftingMode;
    @GuiSync(96)
    public boolean substitute;

    public WPTContainer(int id, final PlayerInventory ip, final WPTGuiObject gui) {
        super(TYPE, id, ip, gui, true);
        wptGUIObject = gui;

        final FixedItemInv patternInv = getPatternTerminal().getInventoryByName("pattern");
        final FixedItemInv output = getPatternTerminal().getInventoryByName("output");

        // Create the 3x3 crafting input grid, which is used for both processing and crafting mode
        craftingGridInv = getPatternTerminal().getInventoryByName("crafting");
        for(int i = 0; i < 9; i++)
            addSlot(craftingGridSlots[i] = new FakeCraftingMatrixSlot(craftingGridInv, i), SlotSemantic.CRAFTING_GRID);


        // Create the output slot used for crafting mode patterns
        addSlot(craftOutputSlot = new PatternTermSlot(ip.player, getActionSource(), powerSource, wptGUIObject, craftingGridInv, patternInv, this, 2, this), SlotSemantic.CRAFTING_RESULT);
        craftOutputSlot.setIcon(null);

        // Create slots for the outputs of processing-mode patterns
        for(int i = 0; i < 3; i++) {
            addSlot(processingOutputSlots[i] = new PatternOutputsSlot(output, this, i, 1), SlotSemantic.PROCESSING_RESULT);
            processingOutputSlots[i].setRenderDisabled(false);
            processingOutputSlots[i].setIcon(null);
        }

        addSlot(blankPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN, patternInv, 0), SlotSemantic.BLANK_PATTERN);
        addSlot(encodedPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patternInv, 1), SlotSemantic.ENCODED_PATTERN);

        encodedPatternSlot.setStackLimit(1);

        final int slotIndex = ((IInventorySlotAware) wptGUIObject).getInventorySlot();
        if(slotIndex < 100) lockPlayerInventorySlot(slotIndex);
        addSlot(new AppEngSlot(new FixedWTInv(getPlayerInventory(), wptGUIObject.getItemStack(), this), FixedWTInv.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);

        if(isClient()) {//FIXME set craftingMode and substitute serverside
            craftingMode = ItemWT.getBoolean(wptGUIObject.getItemStack(), "craftingMode");
            substitute = ItemWT.getBoolean(wptGUIObject.getItemStack(), "substitute");

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString("PatternTerminal.CraftMode");
            int i;
            if(craftingMode) i = 1;
            else i = 0;
            buf.writeByte(i);
            ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
            buf = PacketByteBufs.create();
            buf.writeString("PatternTerminal.Substitute");
            if(substitute) i = 1;
            else i = 0;
            buf.writeByte(i);
            ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
        }
    }

    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(!wptGUIObject.rangeCheck()) {
            if(isValidContainer()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
            setValidContainer(false);
        } else {
            double powerMultiplier = Config.getPowerMultiplier(wptGUIObject.getRange(), wptGUIObject.isOutOfRange());
            ticks++;
            if(ticks > 10) {
                wptGUIObject.extractAEPower((powerMultiplier) * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
                ticks = 0;
            }

            if(wptGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidContainer()) {
                    getPlayerInventory().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                    ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
                }
                setValidContainer(false);
            }
        }

        if(isCraftingMode() != getPatternTerminal().isCraftingRecipe()) {
            setCraftingMode(getPatternTerminal().isCraftingRecipe());
            updateOrderOfOutputSlots();
        }

        if(substitute != getPatternTerminal().isSubstitution()) {
            substitute = getPatternTerminal().isSubstitution();
            ItemWT.setBoolean(wptGUIObject.getItemStack(), substitute, "substitute");
        }
    }

    @Override
    public void onSlotChange(final Slot s) {
        if(s == encodedPatternSlot && isServer()) {
            for(final ScreenHandlerListener listener : ((ScreenHandlerMixin) this).getListeners()) {
                for(int i = 0; i < slots.size(); i++) {
                    Slot slot = slots.get(i);
                    if(slot instanceof OptionalFakeSlot || slot instanceof FakeCraftingMatrixSlot)
                        listener.onSlotUpdate(this, i, slot.getStack());
                }
                if(listener instanceof ServerPlayerEntity)
                    ((ServerPlayerEntity) listener).skipPacketSlotUpdates = false;
            }
            sendContentUpdates();
        }

        if(s == craftOutputSlot && isClient()) getAndUpdateOutput();

        if(isClient() && isCraftingMode()) {
            for(Slot slot : craftingGridSlots) if(s == slot) getAndUpdateOutput();
            for(Slot slot : processingOutputSlots) if(s == slot) getAndUpdateOutput();
        }
    }

    private void setSlotX(Slot s, int x) {
        ((SlotMixin) s).setX(x);
    }

    private void updateOrderOfOutputSlots() {
        if(!isCraftingMode()) {
            setSlotX(craftOutputSlot, -9000);

            for(int y = 0; y < 3; y++) setSlotX(processingOutputSlots[y], processingOutputSlots[y].x);
        } else {
            setSlotX(craftOutputSlot, craftOutputSlot.x);

            for(int y = 0; y < 3; y++) setSlotX(processingOutputSlots[y], -9000);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(FixedItemInv inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {}

    public void encode() {
        ItemStack output = encodedPatternSlot.getStack();

        final ItemStack[] in = getInputs();
        final ItemStack[] out = getOutputs();

        // if there is no input, this would be silly.
        if(in == null || out == null || isCraftingMode() && currentRecipe == null) return;

        // first check the output slots, should either be null, or a pattern
        if(!output.isEmpty() && !craftingHelper.isEncodedPattern(output))
            return; //if nothing is there we should snag a new pattern.
        else if(output.isEmpty()) {
            output = blankPatternSlot.getStack();
            if(output.isEmpty() || !isPattern(output)) return; // no blanks.

            // remove one, and clear the input slot.
            output.setCount(output.getCount() - 1);
            if(output.getCount() == 0) blankPatternSlot.setStack(ItemStack.EMPTY);

            // let the crafting helper create a new encoded pattern
            output = null;
        }

        if(isCraftingMode())
            output = craftingHelper.encodeCraftingPattern(output, currentRecipe, in, out[0], isSubstitute());
        else output = craftingHelper.encodeProcessingPattern(output, in, out);
        encodedPatternSlot.setStack(output);
    }

    private ItemStack[] getInputs() {
        final ItemStack[] input = new ItemStack[9];
        boolean hasValue = false;

        for(int x = 0; x < craftingGridSlots.length; x++) {
            input[x] = craftingGridSlots[x].getStack();
            if(!input[x].isEmpty()) hasValue = true;
        }

        if(hasValue) return input;
        return null;
    }

    private ItemStack[] getOutputs() {
        if(isCraftingMode()) {
            final ItemStack out = getAndUpdateOutput();
            if(!out.isEmpty() && out.getCount() > 0) return new ItemStack[]{out};
        } else {
            boolean hasValue = false;
            final ItemStack[] list = new ItemStack[3];

            for(int i = 0; i < processingOutputSlots.length; i++) {
                final ItemStack out = processingOutputSlots[i].getStack();
                list[i] = out;
                if(!out.isEmpty()) hasValue = true;
            }
            if(hasValue) return list;
        }

        return null;
    }

    @Override
    public FixedItemInv getInventoryByName(final String name) {
        if(name.equals("player")) return new FixedInventoryVanillaWrapper(getPlayerInventory());
        return getPatternTerminal().getInventoryByName(name);
    }

    private boolean isPattern(final ItemStack output) {
        if(output.isEmpty()) return false;

        final IDefinitions definitions = Api.instance().definitions();
        return definitions.materials().blankPattern().isSameAs(output);
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if(idx == 1) return isServer() ? !getPatternTerminal().isCraftingRecipe() : !isCraftingMode();
        else if(idx == 2) return isServer() ? getPatternTerminal().isCraftingRecipe() : isCraftingMode();
        else return false;
    }

    public void craftOrGetItem(PatternSlotPacket packetPatternSlot) {
        if(packetPatternSlot.slotItem == null || monitor == null) return;
        IAEItemStack out = packetPatternSlot.slotItem.copy();
        InventoryAdaptor inv = new AdaptorFixedInv(new WrapperCursorItemHandler(getPlayerInventory().player.inventory));
        InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(getPlayerInventory().player);
        if(packetPatternSlot.shift) inv = playerInv;

        if(!inv.simulateAdd(out.createItemStack()).isEmpty()) return;

        IAEItemStack extracted = Platform.poweredExtraction(powerSource, monitor, out, getActionSource());
        PlayerEntity p = getPlayerInventory().player;
        if(extracted != null) {
            inv.addItems(extracted.createItemStack());
            if(p instanceof ServerPlayerEntity) updateHeld((ServerPlayerEntity) p);

            sendContentUpdates();
            return;
        }

        CraftingInventory ic = new CraftingInventory(new ContainerNull(), 3, 3);
        CraftingInventory real = new CraftingInventory(new ContainerNull(), 3, 3);

        for(int x = 0; x < 9; ++x)
            ic.setStack(x, packetPatternSlot.pattern[x] == null ? ItemStack.EMPTY : packetPatternSlot.pattern[x].createItemStack());

        Recipe<CraftingInventory> r = p.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, p.world).orElse(null);
        if(r == null) return;

        IMEMonitor<IAEItemStack> storage = getPatternTerminal().getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
        IItemList<IAEItemStack> all = storage.getStorageList();
        ItemStack is = r.craft(ic);

        for(int x = 0; x < ic.size(); ++x) {
            if(!ic.getStack(x).isEmpty()) {
                ItemStack pulled = Platform.extractItemsByRecipe(powerSource, getActionSource(), storage, p.world, r, is, ic, ic.getStack(x), x, all, Actionable.MODULATE, ViewCellItem.createFilter(getViewCells()));
                real.setStack(x, pulled);
            }
        }

        Recipe<CraftingInventory> rr = p.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, real, p.world).orElse(null);
        if(rr == r && Platform.itemComparisons().isSameItem(rr.craft(real), is)) {
            CraftingResultInventory craftingResult = new CraftingResultInventory();
            craftingResult.setLastRecipe(rr);
            CraftingResultSlot sc = new CraftingResultSlot(p, real, craftingResult, 0, 0, 0);
            sc.onTakeItem(p, is);

            for(int x = 0; x < real.size(); ++x) {
                ItemStack failed = playerInv.addItems(real.getStack(x));
                if(!failed.isEmpty()) p.dropItem(failed, false);
            }

            inv.addItems(is);
            if(p instanceof ServerPlayerEntity) updateHeld((ServerPlayerEntity) p);

            sendContentUpdates();
        } else {
            for(int x = 0; x < real.size(); ++x) {
                ItemStack failed = real.getStack(x);
                if(!failed.isEmpty())
                    monitor.injectItems(AEItemStack.fromItemStack(failed), Actionable.MODULATE, new MachineSource(getPatternTerminal()));
            }
        }
    }

    private ItemStack getAndUpdateOutput() {
        final World world = getPlayerInventory().player.world;
        final CraftingInventory ic = new CraftingInventory(this, 3, 3);

        for(int x = 0; x < ic.size(); x++) ic.setStack(x, craftingGridInv.getInvStack(x));

        if(currentRecipe == null || !currentRecipe.matches(ic, world))
            currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);

        final ItemStack is;

        if(currentRecipe == null) is = ItemStack.EMPTY;
        else is = currentRecipe.craft(ic);

        craftOutputSlot.setDisplayedCraftingOutput(is);
        return is;
    }

    @Override
    public boolean useRealItems() {
        return false;
    }

    public WPTGuiObject getPatternTerminal() {
        return wptGUIObject;
    }

    private boolean isSubstitute() {
        return substitute;
    }

    public boolean isCraftingMode() {
        return craftingMode;
    }

    private void setCraftingMode(final boolean craftingMode) {
        if(craftingMode == this.craftingMode) return;
        this.craftingMode = craftingMode;
        ItemWT.setBoolean(wptGUIObject.getItemStack(), craftingMode, "craftingMode");
    }

    public void clear() {
        for(final Slot s : craftingGridSlots) s.setStack(ItemStack.EMPTY);
        for(final Slot s : processingOutputSlots) s.setStack(ItemStack.EMPTY);

        sendContentUpdates();
        getAndUpdateOutput();
    }

    @Override
    public IGridNode getNetworkNode() {
        return wptGUIObject.getActionableNode();
    }

    public boolean isWUT() {
        return wptGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public List<ItemStack> getViewCells() {
        return wptGUIObject.getViewCellStorage().getViewCells();
    }
}