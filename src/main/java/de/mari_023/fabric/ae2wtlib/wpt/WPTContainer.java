package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.api.config.Actionable;
import appeng.api.crafting.ICraftingHelper;
import appeng.api.definitions.IDefinitions;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.ContainerLocator;
import appeng.container.ContainerNull;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.MEPortableCellContainer;
import appeng.container.slot.*;
import appeng.core.AEConfig;
import appeng.core.Api;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.packets.PatternSlotPacket;
import appeng.helpers.IContainerCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.me.helpers.MachineSource;
import appeng.mixins.SlotMixin;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.AdaptorFixedInv;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.inv.WrapperCursorItemHandler;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.ContainerHelper;
import de.mari_023.fabric.ae2wtlib.wct.FixedWCTInv;
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
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WPTContainer extends MEPortableCellContainer implements IAEAppEngInventory, IOptionalSlotHost, IContainerCraftingPacket {

    public static ScreenHandlerType<WPTContainer> TYPE;

    public static final ContainerHelper<WPTContainer, WPTGuiObject> helper = new ContainerHelper<>(WPTContainer::new, WPTGuiObject.class);

    public static WPTContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    private final FakeCraftingMatrixSlot[] craftingSlots = new FakeCraftingMatrixSlot[9];
    private final OptionalFakeSlot[] outputSlots = new OptionalFakeSlot[3];
    private CraftingRecipe currentRecipe;
    private final AppEngInternalInventory cOut = new AppEngInternalInventory(null, 1);
    private final FixedItemInv crafting;
    private final PatternTermSlot craftSlot;
    private final RestrictedInputSlot patternSlotIN;
    private final RestrictedInputSlot patternSlotOUT;
    private final ICraftingHelper craftingHelper = Api.INSTANCE.crafting();

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WPTGuiObject wptGUIObject;

    @GuiSync(97)
    public boolean craftingMode = true;
    @GuiSync(96)
    public boolean substitute = false;


    public WPTContainer(int id, final PlayerInventory ip, final WPTGuiObject gui) {
        super(TYPE, id, ip, gui);
        wptGUIObject = gui;

        final FixedItemInv patternInv = getPatternTerminal().getInventoryByName("pattern");
        final FixedItemInv output = getPatternTerminal().getInventoryByName("output");

        final FixedWCTInv fixedWPTInv = new FixedWCTInv(getPlayerInv(), wptGUIObject.getItemStack());

        crafting = getPatternTerminal().getInventoryByName("crafting");

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                addSlot(craftingSlots[x + y * 3] = new FakeCraftingMatrixSlot(crafting, x + y * 3,
                        18 + x * 18, -76 + y * 18));
            }
        }

        addSlot(craftSlot = new PatternTermSlot(ip.player, getActionSource(), getPowerSource(),
                gui, crafting, patternInv, cOut, 110, -76 + 18, this, 2, this));
        craftSlot.setIIcon(-1);

        for(int y = 0; y < 3; y++) {
            addSlot(outputSlots[y] = new PatternOutputsSlot(output, this, y, 110, -76 + y * 18, 0, 0, 1));
            outputSlots[y].setRenderDisabled(false);
            outputSlots[y].setIIcon(-1);
        }

        //infinityBoosterCard
        addSlot(new AppEngSlot(fixedWPTInv, 6, 80, -20));

        addSlot(patternSlotIN = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN,
                patternInv, 0, 147, -72 - 9, getPlayerInventory()));
        addSlot(patternSlotOUT = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
                patternInv, 1, 147, -72 + 34, getPlayerInventory()));
    }

    @Override
    public void sendContentUpdates() {
        if(isServer()) {
            super.sendContentUpdates();

            if(!wptGUIObject.rangeCheck()) {
                if(isValidContainer()) {
                    getPlayerInv().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                    close(getPlayerInv().player);//TODO fix Inventory still being open
                }

                setValidContainer(false);
            } else {
                double powerMultiplier = AEConfig.instance().wireless_getDrainRate(wptGUIObject.getRange());
                try {
                    Method method = super.getClass().getDeclaredMethod("setPowerMultiplier", double.class);
                    method.setAccessible(true);
                    method.invoke(this, powerMultiplier);
                    method.setAccessible(false);
                } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
            }

            if(isCraftingMode() != getPatternTerminal().isCraftingRecipe()) {
                setCraftingMode(getPatternTerminal().isCraftingRecipe());
                updateOrderOfOutputSlots();
            }

            substitute = getPatternTerminal().isSubstitution();
        }
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        super.onUpdate(field, oldValue, newValue);

        if(field.equals("craftingMode")) {
            getAndUpdateOutput();
            updateOrderOfOutputSlots();
        }
    }

    @Override
    public void onSlotChange(final Slot s) {
        if(s == patternSlotOUT && isServer()) {
            for(final ScreenHandlerListener listener : getListeners()) {
                for(int i = 0; i < slots.size(); i++) {
                    Slot slot = slots.get(i);
                    if(slot instanceof OptionalFakeSlot || slot instanceof FakeCraftingMatrixSlot) {
                        listener.onSlotUpdate(this, i, slot.getStack());
                    }
                }
                if(listener instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) listener).skipPacketSlotUpdates = false;
                }
            }
            sendContentUpdates();
        }

        if(s == craftSlot && isClient()) {
            getAndUpdateOutput();
        }
    }

    private void setSlotX(Slot s, int x) {
        ((SlotMixin) s).setX(x);
    }

    private void updateOrderOfOutputSlots() {
        if(!isCraftingMode()) {
            setSlotX(craftSlot, -9000);

            for(int y = 0; y < 3; y++) {
                setSlotX(outputSlots[y], outputSlots[y].getX());
            }
        } else {
            setSlotX(craftSlot, craftSlot.getX());

            for(int y = 0; y < 3; y++) {
                setSlotX(outputSlots[y], -9000);
            }
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
        ItemStack output = patternSlotOUT.getStack();

        final ItemStack[] in = getInputs();
        final ItemStack[] out = getOutputs();

        // if there is no input, this would be silly.
        if(in == null || out == null || isCraftingMode() && currentRecipe == null) {
            return;
        }

        // first check the output slots, should either be null, or a pattern
        if(!output.isEmpty() && !craftingHelper.isEncodedPattern(output)) {
            return;
        } // if nothing is there we should snag a new pattern.
        else if(output.isEmpty()) {
            output = patternSlotIN.getStack();
            if(output.isEmpty() || !isPattern(output)) {
                return; // no blanks.
            }

            // remove one, and clear the input slot.
            output.setCount(output.getCount() - 1);
            if(output.getCount() == 0) {
                patternSlotIN.setStack(ItemStack.EMPTY);
            }

            // let the crafting helper create a new encoded pattern
            output = null;
        }

        if(isCraftingMode()) {
            output = craftingHelper.encodeCraftingPattern(output, currentRecipe, in, out[0], isSubstitute());
        } else {
            output = craftingHelper.encodeProcessingPattern(output, in, out);
        }
        patternSlotOUT.setStack(output);

    }

    private ItemStack[] getInputs() {
        final ItemStack[] input = new ItemStack[9];
        boolean hasValue = false;

        for(int x = 0; x < craftingSlots.length; x++) {
            input[x] = craftingSlots[x].getStack();
            if(!input[x].isEmpty()) {
                hasValue = true;
            }
        }

        if(hasValue) {
            return input;
        }

        return null;
    }

    private ItemStack[] getOutputs() {
        if(isCraftingMode()) {
            final ItemStack out = getAndUpdateOutput();

            if(!out.isEmpty() && out.getCount() > 0) {
                return new ItemStack[]{out};
            }
        } else {
            boolean hasValue = false;
            final ItemStack[] list = new ItemStack[3];

            for(int i = 0; i < outputSlots.length; i++) {
                final ItemStack out = outputSlots[i].getStack();
                list[i] = out;
                if(!out.isEmpty()) {
                    hasValue = true;
                }
            }
            if(hasValue) {
                return list;
            }
        }

        return null;
    }

    @Override
    public FixedItemInv getInventoryByName(final String name) {
        if(name.equals("player")) {
            return new FixedInventoryVanillaWrapper(getPlayerInventory());
        }
        return getPatternTerminal().getInventoryByName(name);
    }

    private boolean isPattern(final ItemStack output) {
        if(output.isEmpty()) {
            return false;
        }

        final IDefinitions definitions = Api.instance().definitions();
        return definitions.materials().blankPattern().isSameAs(output);
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if(idx == 1) {
            return isServer() ? !getPatternTerminal().isCraftingRecipe() : !isCraftingMode();
        } else if(idx == 2) {
            return isServer() ? getPatternTerminal().isCraftingRecipe() : isCraftingMode();
        } else {
            return false;
        }
    }

    public void craftOrGetItem(final PatternSlotPacket packetPatternSlot) {
        if(packetPatternSlot.slotItem != null && getCellInventory() != null) {
            final IAEItemStack out = packetPatternSlot.slotItem.copy();
            InventoryAdaptor inv = new AdaptorFixedInv(
                    new WrapperCursorItemHandler(getPlayerInv().player.inventory));
            final InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(getPlayerInv().player);

            if(packetPatternSlot.shift) {
                inv = playerInv;
            }

            if(!inv.simulateAdd(out.createItemStack()).isEmpty()) {
                return;
            }

            final IAEItemStack extracted = Platform.poweredExtraction(getPowerSource(), getCellInventory(),
                    out, getActionSource());
            final PlayerEntity p = getPlayerInv().player;

            if(extracted != null) {
                inv.addItems(extracted.createItemStack());
                if(p instanceof ServerPlayerEntity) {
                    updateHeld((ServerPlayerEntity) p);
                }
                sendContentUpdates();
                return;
            }

            final CraftingInventory ic = new CraftingInventory(new ContainerNull(), 3, 3);
            final CraftingInventory real = new CraftingInventory(new ContainerNull(), 3, 3);

            for(int x = 0; x < 9; x++) {
                ic.setStack(x, packetPatternSlot.pattern[x] == null ? ItemStack.EMPTY
                        : packetPatternSlot.pattern[x].createItemStack());
            }

            final Recipe<CraftingInventory> r = p.world.getRecipeManager()
                    .getFirstMatch(RecipeType.CRAFTING, ic, p.world).orElse(null);

            if(r == null) {
                return;
            }

            final IMEMonitor<IAEItemStack> storage = getPatternTerminal()
                    .getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
            final IItemList<IAEItemStack> all = storage.getStorageList();

            final ItemStack is = r.craft(ic);

            for(int x = 0; x < ic.size(); x++) {
                if(!ic.getStack(x).isEmpty()) {
                    final ItemStack pulled = Platform.extractItemsByRecipe(getPowerSource(),
                            getActionSource(), storage, p.world, r, is, ic, ic.getStack(x), x, all,
                            Actionable.MODULATE, ViewCellItem.createFilter(getViewCells()));
                    real.setStack(x, pulled);
                }
            }

            final Recipe<CraftingInventory> rr = p.world.getRecipeManager()
                    .getFirstMatch(RecipeType.CRAFTING, real, p.world).orElse(null);

            if(rr == r && Platform.itemComparisons().isSameItem(rr.craft(real), is)) {
                final CraftingResultInventory craftingResult = new CraftingResultInventory();
                craftingResult.setLastRecipe(rr);

                final CraftingResultSlot sc = new CraftingResultSlot(p, real, craftingResult, 0, 0, 0);
                sc.onTakeItem(p, is);

                for(int x = 0; x < real.size(); x++) {
                    final ItemStack failed = playerInv.addItems(real.getStack(x));

                    if(!failed.isEmpty()) {
                        p.dropItem(failed, false);
                    }
                }

                inv.addItems(is);
                if(p instanceof ServerPlayerEntity) {
                    updateHeld((ServerPlayerEntity) p);
                }
                sendContentUpdates();
            } else {
                for(int x = 0; x < real.size(); x++) {
                    final ItemStack failed = real.getStack(x);
                    if(!failed.isEmpty()) {
                        getCellInventory().injectItems(AEItemStack.fromItemStack(failed), Actionable.MODULATE,
                                new MachineSource(getPatternTerminal()));
                    }
                }
            }
        }
    }

    private ItemStack getAndUpdateOutput() {
        final World world = getPlayerInv().player.world;
        final CraftingInventory ic = new CraftingInventory(this, 3, 3);

        for(int x = 0; x < ic.size(); x++) {
            ic.setStack(x, crafting.getInvStack(x));
        }

        if(currentRecipe == null || !currentRecipe.matches(ic, world)) {
            currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);
        }

        final ItemStack is;

        if(currentRecipe == null) {
            is = ItemStack.EMPTY;
        } else {
            is = currentRecipe.craft(ic);
        }

        cOut.forceSetInvStack(0, is);
        return is;
    }

    @Override
    public boolean useRealItems() {
        return false;
    }

    public WPTGuiObject getPatternTerminal() {
        return wptGUIObject;
    }

    public void toggleSubstitute() {
        substitute = !substitute;

        sendContentUpdates();
        getAndUpdateOutput();
    }

    private boolean isSubstitute() {
        return substitute;
    }

    public boolean isCraftingMode() {
        return craftingMode;
    }

    private void setCraftingMode(final boolean craftingMode) {
        this.craftingMode = craftingMode;
    }

    public void clear() {
        for(final Slot s : craftingSlots) {
            s.setStack(ItemStack.EMPTY);
        }

        for(final Slot s : outputSlots) {
            s.setStack(ItemStack.EMPTY);
        }

        sendContentUpdates();
        getAndUpdateOutput();
    }

    /*@Override
    public ItemStack[] getViewCells() {
        return wctGUIObject.getViewCellStorage().getViewCells();
    }*/
}