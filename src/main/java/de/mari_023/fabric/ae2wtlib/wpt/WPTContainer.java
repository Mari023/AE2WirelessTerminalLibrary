package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.crafting.IPatternDetailsHelper;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackList;
import appeng.core.definitions.AEItems;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.packets.PatternSlotPacket;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.misc.FluidDummyItem;
import appeng.items.storage.ViewCellItem;
import appeng.me.helpers.MachineSource;
import appeng.menu.NullMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.interfaces.IInventorySlotAware;
import appeng.menu.me.items.ItemTerminalMenu;
import appeng.menu.slot.*;
import appeng.parts.reporting.PatternTerminalPart;
import appeng.util.Platform;
import appeng.util.inv.CarriedItemInventory;
import appeng.util.inv.PlayerInternalInventory;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WPTContainer extends ItemTerminalMenu implements IOptionalSlotHost, IMenuCraftingPacket, IWTInvHolder {

    public static final ScreenHandlerType<WPTContainer> TYPE = MenuTypeBuilder.create(WPTContainer::new, WPTGuiObject.class).requirePermission(SecurityPermissions.CRAFT).build("wireless_pattern_terminal");

    private static final String ACTION_SET_CRAFT_MODE = "setCraftMode";
    private static final String ACTION_ENCODE = "encode";
    private static final String ACTION_CLEAR = "clear";
    private static final String ACTION_SET_SUBSTITUTION = "setSubstitution";
    private static final String ACTION_CONVERT_ITEMS_TO_FLUIDS = "convertItemsToFluids";

    private final InternalInventory craftingGridInv;
    private final FakeCraftingMatrixSlot[] craftingGridSlots = new FakeCraftingMatrixSlot[9];
    private final OptionalFakeSlot[] processingOutputSlots = new OptionalFakeSlot[3];
    private final PatternTermSlot craftOutputSlot;
    private final RestrictedInputSlot blankPatternSlot;
    private final RestrictedInputSlot encodedPatternSlot;
    private CraftingRecipe currentRecipe;
    private boolean currentRecipeCraftingMode;
    private final IPatternDetailsHelper craftingHelper = AEApi.patterns();

    private final WPTGuiObject wptGUIObject;

    @GuiSync(97)
    public boolean craftingMode;
    @GuiSync(96)
    public boolean substitute;

    public WPTContainer(int id, final PlayerInventory ip, final WPTGuiObject gui) {
        super(TYPE, id, ip, gui, true);
        wptGUIObject = gui;

        final InternalInventory patternInv = getPatternTerminal().getSubInventory(ISegmentedInventory.PATTERNS);
        final InternalInventory output = getPatternTerminal().getSubInventory(PatternTerminalPart.INV_OUTPUT);

        // Create the 3x3 crafting input grid, which is used for both processing and crafting mode
        craftingGridInv = getPatternTerminal().getSubInventory(PatternTerminalPart.INV_CRAFTING);
        for(int i = 0; i < 9; i++)
            addSlot(craftingGridSlots[i] = new FakeCraftingMatrixSlot(craftingGridInv, i), SlotSemantic.CRAFTING_GRID);


        // Create the output slot used for crafting mode patterns
        addSlot(craftOutputSlot = new PatternTermSlot(ip.player, getActionSource(), powerSource, wptGUIObject, craftingGridInv, patternInv, this, 2, this), SlotSemantic.CRAFTING_RESULT);
        craftOutputSlot.setIcon(null);

        // Create slots for the outputs of processing-mode patterns

        addSlot(processingOutputSlots[0] = new PatternOutputsSlot(output, this, 0, 1), SlotSemantic.PROCESSING_PRIMARY_RESULT);
        addSlot(processingOutputSlots[1] = new PatternOutputsSlot(output, this, 1, 1), SlotSemantic.PROCESSING_FIRST_OPTIONAL_RESULT);
        addSlot(processingOutputSlots[2] = new PatternOutputsSlot(output, this, 2, 1), SlotSemantic.PROCESSING_SECOND_OPTIONAL_RESULT);

        for(int i = 0; i < 3; ++i) {
            processingOutputSlots[i].setRenderDisabled(false);
            processingOutputSlots[i].setIcon(null);
        }

        addSlot(blankPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN, patternInv, 0), SlotSemantic.BLANK_PATTERN);
        addSlot(encodedPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patternInv, 1), SlotSemantic.ENCODED_PATTERN);

        encodedPatternSlot.setStackLimit(1);

        final int slotIndex = ((IInventorySlotAware) wptGUIObject).getInventorySlot();
        if(slotIndex < 100 && slotIndex != 40) lockPlayerInventorySlot(slotIndex);
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
            ClientPlayNetworking.send(new Identifier(ae2wtlib.MOD_NAME, "general"), buf);
            buf = PacketByteBufs.create();
            buf.writeString("PatternTerminal.Substitute");
            if(substitute) i = 1;
            else i = 0;
            buf.writeByte(i);
            ClientPlayNetworking.send(new Identifier(ae2wtlib.MOD_NAME, "general"), buf);
        }
        registerClientAction(ACTION_ENCODE, this::encode);
        registerClientAction(ACTION_CLEAR, this::clear);
        registerClientAction(ACTION_SET_CRAFT_MODE, Boolean.class, getPatternTerminal()::setCraftingRecipe);
        registerClientAction(ACTION_SET_SUBSTITUTION, Boolean.class, getPatternTerminal()::setSubstitution);
        registerClientAction(ACTION_CONVERT_ITEMS_TO_FLUIDS, this::convertItemsToFluids);
    }

    @Override
    public void setStackInSlot(int slotID, int stateId, ItemStack stack) {
        super.setStackInSlot(slotID, stateId, stack);
        getAndUpdateOutput();
    }

    private ItemStack getAndUpdateOutput() {
        final World world = getPlayerInventory().player.world;
        final CraftingInventory ic = new CraftingInventory(this, 3, 3);

        for(int x = 0; x < ic.size(); x++) ic.setStack(x, craftingGridInv.getStackInSlot(x));

        if(currentRecipe == null || !currentRecipe.matches(ic, world)) {
            currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);
            currentRecipeCraftingMode = craftingMode;
        }

        final ItemStack is;
        if(currentRecipe == null) is = ItemStack.EMPTY;
        else is = currentRecipe.craft(ic);

        craftOutputSlot.setDisplayedCraftingOutput(is);
        return is;
    }

    public void encode() {
        if(isClient()) {
            sendClientAction(ACTION_ENCODE);
            return;
        }
        ItemStack output = encodedPatternSlot.getStack();
        ItemStack[] in = getInputs();
        ItemStack[] out = getOutputs();
        if(in != null && out != null && (!isCraftingMode() || currentRecipe != null)) {
            if(output.isEmpty() || craftingHelper.isEncodedPattern(output)) {
                if(output.isEmpty()) {
                    output = blankPatternSlot.getStack();
                    if(output.isEmpty() || !isPattern(output)) {
                        return;
                    }

                    output.setCount(output.getCount() - 1);
                    if(output.getCount() == 0) {
                        blankPatternSlot.setStack(ItemStack.EMPTY);
                    }
                }

                if(isCraftingMode()) {
                    output = craftingHelper.encodeCraftingPattern(currentRecipe, in, out[0], isSubstitute());
                } else {
                    output = craftingHelper.encodeProcessingPattern(toAeStacks(in), toAeStacks(out));
                }

                encodedPatternSlot.setStack(output);
            }
        }
    }

    private static IAEStack[] toAeStacks(ItemStack... stacks) {
        IAEStack[] out = new IAEStack[stacks.length];
        var fluidDummy = AEItems.DUMMY_FLUID_ITEM.asItem();
        for(int i = 0; i < stacks.length; ++i) {
            if(stacks[i].getItem() == fluidDummy) {
                out[i] = IAEFluidStack.of(fluidDummy.getFluid(stacks[i]), fluidDummy.getAmount(stacks[i]));
            } else {
                out[i] = AEItemStack.fromItemStack(stacks[i]);
            }
        }
        return out;
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

    private boolean isPattern(final ItemStack output) {
        return !output.isEmpty() && AEItems.BLANK_PATTERN.isSameAs(output);
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if(idx == 1) return isServer() ? !getPatternTerminal().isCraftingRecipe() : !isCraftingMode();
        else if(idx == 2) return isServer() ? getPatternTerminal().isCraftingRecipe() : isCraftingMode();
        else return false;
    }

    public void craftOrGetItem(final PatternSlotPacket packetPatternSlot) {
        if(packetPatternSlot.slotItem == null || monitor == null) return;
        final IAEItemStack out = packetPatternSlot.slotItem.copy();
        InternalInventory inv = new CarriedItemInventory(this);
        PlayerInternalInventory playerInv = new PlayerInternalInventory(getPlayerInventory());
        if(packetPatternSlot.shift) inv = playerInv;

        if(!inv.simulateAdd(out.createItemStack()).isEmpty()) return;

        final IAEItemStack extracted = Platform.poweredExtraction(powerSource, monitor, out, getActionSource());
        final PlayerEntity p = getPlayerInventory().player;

        if(extracted != null) {
            inv.addItems(extracted.createItemStack());
            sendContentUpdates();
            return;
        }

        final CraftingInventory ic = new CraftingInventory(new NullMenu(), 3, 3);
        final CraftingInventory real = new CraftingInventory(new NullMenu(), 3, 3);

        for(int x = 0; x < 9; x++) {
            ic.setStack(x, packetPatternSlot.pattern[x] == null ? ItemStack.EMPTY : packetPatternSlot.pattern[x].createItemStack());
        }

        final Recipe<CraftingInventory> r = p.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, p.world).orElse(null);

        if(r == null) return;

        final IMEMonitor<IAEItemStack> storage = getPatternTerminal().getInventory(StorageChannels.items());
        final IAEStackList<IAEItemStack> all = storage.getStorageList();

        final ItemStack is = r.craft(ic);

        for(int x = 0; x < ic.size(); x++) {
            if(!ic.getStack(x).isEmpty()) {
                final ItemStack pulled = Platform.extractItemsByRecipe(powerSource, getActionSource(), storage, p.world, r, is, ic, ic.getStack(x), x, all, Actionable.MODULATE, ViewCellItem.createFilter(getViewCells()));
                real.setStack(x, pulled);
            }
        }

        final Recipe<CraftingInventory> rr = p.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, real, p.world).orElse(null);

        if(rr == r && ItemStack.canCombine(rr.craft(real), is)) {
            final CraftingResultInventory craftingResult = new CraftingResultInventory();
            craftingResult.setLastRecipe(rr);

            final CraftingResultSlot sc = new CraftingResultSlot(p, real, craftingResult, 0, 0, 0);
            sc.onTakeItem(p, is);

            for(int x = 0; x < real.size(); x++) {
                final ItemStack failed = playerInv.addItems(real.getStack(x));

                if(!failed.isEmpty()) p.dropItem(failed, false);
            }

            inv.addItems(is);
            sendContentUpdates();
        } else {
            for(int x = 0; x < real.size(); ++x) {
                final ItemStack failed = real.getStack(x);
                if(failed.isEmpty()) continue;
                monitor.injectItems(AEItemStack.fromItemStack(failed), Actionable.MODULATE, new MachineSource(getPatternTerminal()));
            }
        }
    }

    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(wptGUIObject.notInRange()) {
            if(isValidMenu()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
            setValidMenu(false);
        } else {
            double powerMultiplier = Config.getPowerMultiplier(wptGUIObject.getRange(), wptGUIObject.isOutOfRange());
            ticks++;
            if(ticks > 10) {
                wptGUIObject.extractAEPower((powerMultiplier) * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
                ticks = 0;
            }

            if(wptGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidMenu()) {
                    getPlayerInventory().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                    ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
                }
                setValidMenu(false);
            }
        }

        if(isCraftingMode() != getPatternTerminal().isCraftingRecipe()) {
            setCraftingMode(getPatternTerminal().isCraftingRecipe());
        }

        if(substitute != getPatternTerminal().isSubstitution()) {
            substitute = getPatternTerminal().isSubstitution();
            ItemWT.setBoolean(wptGUIObject.getItemStack(), substitute, "substitute");
        }
    }

    @Override
    public void onServerDataSync() {
        super.onServerDataSync();

        if(currentRecipeCraftingMode != craftingMode) getAndUpdateOutput();
    }

    @Override
    public void onSlotChange(final Slot s) {
        if(s == encodedPatternSlot && isServer()) sendContentUpdates();

        if(s == craftOutputSlot && isClient()) getAndUpdateOutput();
    }

    public void clear() {
        if(isClient()) {
            sendClientAction(ACTION_CLEAR);
            return;
        }
        for(final Slot s : craftingGridSlots) s.setStack(ItemStack.EMPTY);
        for(final Slot s : processingOutputSlots) s.setStack(ItemStack.EMPTY);

        sendContentUpdates();
        getAndUpdateOutput();
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        return getPatternTerminal().getSubInventory(id);
    }

    @Override
    public boolean useRealItems() {
        return false;
    }

    public boolean isCraftingMode() {
        return craftingMode;
    }

    private void setCraftingMode(final boolean craftingMode) {
        if(isClient()) {
            sendClientAction(ACTION_SET_CRAFT_MODE, craftingMode);
            return;
        }
        if(craftingMode == this.craftingMode) return;
        this.craftingMode = craftingMode;
        ItemWT.setBoolean(wptGUIObject.getItemStack(), craftingMode, "craftingMode");
    }

    public WPTGuiObject getPatternTerminal() {
        return wptGUIObject;
    }

    private boolean isSubstitute() {
        return substitute;
    }

    public void setSubstitute(final boolean substitute) {
        if(isClient()) sendClientAction(ACTION_SET_SUBSTITUTION, substitute);
        else this.substitute = substitute;
    }

    public void convertItemsToFluids() {
        if(isClient()) {
            sendClientAction(ACTION_CONVERT_ITEMS_TO_FLUIDS);
            return;
        }
        if(getPatternTerminal().isCraftingRecipe()) return;
        for(var slot : craftingGridSlots) {
            convertItemToFluid(slot);
        }
        for(var slot : processingOutputSlots) {
            convertItemToFluid(slot);
        }
    }

    /**
     * @return True, if any slot can be converted from item->fluid.
     */
    public boolean canConvertItemsToFluids() {
        if(isCraftingMode()) return false;

        for(var slot : craftingGridSlots) {
            if(canConvertItemToFluid(slot)) return true;
        }
        for(var slot : processingOutputSlots) {
            if(canConvertItemToFluid(slot)) return true;
        }
        return false;
    }

    private static void convertItemToFluid(Slot slot) {
        var fluidStack = getFluidContained(slot.getStack());
        if(fluidStack != null) slot.setStack(FluidDummyItem.fromFluidStack(fluidStack, true));
    }

    private static boolean canConvertItemToFluid(Slot slot) {
        return getFluidContained(slot.getStack()) != null;
    }

    @Nullable
    private static ResourceAmount<FluidVariant> getFluidContained(ItemStack stack) {
        return stack.isEmpty() ? null : StorageUtil.findExtractableContent(ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM), null);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
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