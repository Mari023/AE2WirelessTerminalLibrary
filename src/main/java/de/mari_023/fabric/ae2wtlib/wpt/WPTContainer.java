package de.mari_023.fabric.ae2wtlib.wpt;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.api.config.Actionable;
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
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

    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 9);
    private final FakeCraftingMatrixSlot[] craftingSlots = new FakeCraftingMatrixSlot[9];
    private final OptionalFakeSlot[] outputSlots = new OptionalFakeSlot[3];
    private Recipe<CraftingInventory> currentRecipe;
    private final AppEngInternalInventory cOut = new AppEngInternalInventory(null, 1);
    private final FixedItemInv crafting;
    private final RestrictedInputSlot patternSlotIN;
    private final RestrictedInputSlot patternSlotOUT;

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

        final FixedItemInv patternInv = this.getPatternTerminal().getInventoryByName("pattern");
        final FixedItemInv output = this.getPatternTerminal().getInventoryByName("output");

        final FixedWCTInv fixedWCTInv = new FixedWCTInv(getPlayerInv(), wptGUIObject.getItemStack());

        crafting = getPatternTerminal().getInventoryByName("crafting");

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(this.craftingSlots[x + y * 3] = new FakeCraftingMatrixSlot(this.crafting, x + y * 3,
                        18 + x * 18, -76 + y * 18));
            }
        }

        for (int y = 0; y < 3; y++) {
            this.addSlot(this.outputSlots[y] = new PatternOutputsSlot(output, this, y, 110, -76 + y * 18, 0, 0, 1));
            this.outputSlots[y].setRenderDisabled(false);
            this.outputSlots[y].setIIcon(-1);
        }

        //infinityBoosterCard
        addSlot(new AppEngSlot(fixedWCTInv, 6, 80, -20));

        this.addSlot(this.patternSlotIN = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN,
                patternInv, 0, 147, -72 - 9, this.getPlayerInventory()));
        this.addSlot(this.patternSlotOUT = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
                patternInv, 1, 147, -72 + 34, this.getPlayerInventory()));
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if(!wptGUIObject.rangeCheck()) {
            if(isServer() && isValidContainer()) {
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

    @Override
    public FixedItemInv getInventoryByName(final String name) {
        if (name.equals("player")) {
            return new FixedInventoryVanillaWrapper(this.getPlayerInventory());
        }
        return this.getPatternTerminal().getInventoryByName(name);
    }

    private boolean isPattern(final ItemStack output) {
        if (output.isEmpty()) {
            return false;
        }

        final IDefinitions definitions = Api.instance().definitions();
        return definitions.materials().blankPattern().isSameAs(output);
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if (idx == 1) {
            return isServer() ? !getPatternTerminal().isCraftingRecipe() : !isCraftingMode();
        } else if (idx == 2) {
            return isServer() ? getPatternTerminal().isCraftingRecipe() : isCraftingMode();
        } else {
            return false;
        }
    }

    public void craftOrGetItem(final PatternSlotPacket packetPatternSlot) {
        if (packetPatternSlot.slotItem != null && this.getCellInventory() != null) {
            final IAEItemStack out = packetPatternSlot.slotItem.copy();
            InventoryAdaptor inv = new AdaptorFixedInv(
                    new WrapperCursorItemHandler(this.getPlayerInv().player.inventory));
            final InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(this.getPlayerInv().player);

            if (packetPatternSlot.shift) {
                inv = playerInv;
            }

            if (!inv.simulateAdd(out.createItemStack()).isEmpty()) {
                return;
            }

            final IAEItemStack extracted = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(),
                    out, this.getActionSource());
            final PlayerEntity p = this.getPlayerInv().player;

            if (extracted != null) {
                inv.addItems(extracted.createItemStack());
                if (p instanceof ServerPlayerEntity) {
                    this.updateHeld((ServerPlayerEntity) p);
                }
                this.sendContentUpdates();
                return;
            }

            final CraftingInventory ic = new CraftingInventory(new ContainerNull(), 3, 3);
            final CraftingInventory real = new CraftingInventory(new ContainerNull(), 3, 3);

            for (int x = 0; x < 9; x++) {
                ic.setStack(x, packetPatternSlot.pattern[x] == null ? ItemStack.EMPTY
                        : packetPatternSlot.pattern[x].createItemStack());
            }

            final Recipe<CraftingInventory> r = p.world.getRecipeManager()
                    .getFirstMatch(RecipeType.CRAFTING, ic, p.world).orElse(null);

            if (r == null) {
                return;
            }

            final IMEMonitor<IAEItemStack> storage = this.getPatternTerminal()
                    .getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
            final IItemList<IAEItemStack> all = storage.getStorageList();

            final ItemStack is = r.craft(ic);

            for (int x = 0; x < ic.size(); x++) {
                if (!ic.getStack(x).isEmpty()) {
                    final ItemStack pulled = Platform.extractItemsByRecipe(this.getPowerSource(),
                            this.getActionSource(), storage, p.world, r, is, ic, ic.getStack(x), x, all,
                            Actionable.MODULATE, ViewCellItem.createFilter(this.getViewCells()));
                    real.setStack(x, pulled);
                }
            }

            final Recipe<CraftingInventory> rr = p.world.getRecipeManager()
                    .getFirstMatch(RecipeType.CRAFTING, real, p.world).orElse(null);

            if (rr == r && Platform.itemComparisons().isSameItem(rr.craft(real), is)) {
                final CraftingResultInventory craftingResult = new CraftingResultInventory();
                craftingResult.setLastRecipe(rr);

                final CraftingResultSlot sc = new CraftingResultSlot(p, real, craftingResult, 0, 0, 0);
                sc.onTakeItem(p, is);

                for (int x = 0; x < real.size(); x++) {
                    final ItemStack failed = playerInv.addItems(real.getStack(x));

                    if (!failed.isEmpty()) {
                        p.dropItem(failed, false);
                    }
                }

                inv.addItems(is);
                if (p instanceof ServerPlayerEntity) {
                    this.updateHeld((ServerPlayerEntity) p);
                }
                this.sendContentUpdates();
            } else {
                for (int x = 0; x < real.size(); x++) {
                    final ItemStack failed = real.getStack(x);
                    if (!failed.isEmpty()) {
                        this.getCellInventory().injectItems(AEItemStack.fromItemStack(failed), Actionable.MODULATE,
                                new MachineSource(this.getPatternTerminal()));
                    }
                }
            }
        }
    }

    private ItemStack getAndUpdateOutput() {
        final World world = this.getPlayerInv().player.world;
        final CraftingInventory ic = new CraftingInventory(this, 3, 3);

        for (int x = 0; x < ic.size(); x++) {
            ic.setStack(x, crafting.getInvStack(x));
        }

        if (this.currentRecipe == null || !this.currentRecipe.matches(ic, world)) {
            this.currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);
        }

        final ItemStack is;

        if (this.currentRecipe == null) {
            is = ItemStack.EMPTY;
        } else {
            is = this.currentRecipe.craft(ic);
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

    public boolean isCraftingMode() {
        return craftingMode;
    }

    private void setCraftingMode(final boolean craftingMode) {
        this.craftingMode = craftingMode;
    }

    /*@Override
    public ItemStack[] getViewCells() {
        return wctGUIObject.getViewCellStorage().getViewCells();
    }*/
}