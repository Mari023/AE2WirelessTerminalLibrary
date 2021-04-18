package de.mari_023.fabric.ae2wtlib.wct;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.container.ContainerLocator;
import appeng.container.ContainerNull;
import appeng.container.implementations.MEMonitorableContainer;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.container.slot.CraftingTermSlot;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.IContainerCraftingPacket;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import com.mojang.datafixers.util.Pair;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.ContainerHelper;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class WCTContainer extends MEMonitorableContainer implements IAEAppEngInventory, IContainerCraftingPacket, IWTInvHolder {

    public static ScreenHandlerType<WCTContainer> TYPE;

    public static final ContainerHelper<WCTContainer, WCTGuiObject> helper = new ContainerHelper<>(WCTContainer::new, WCTGuiObject.class);

    public static WCTContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    private final AppEngInternalInventory craftingGrid;
    private final CraftingMatrixSlot[] craftingSlots = new CraftingMatrixSlot[9];
    private final CraftingTermSlot outputSlot;
    private Recipe<CraftingInventory> currentRecipe;
    final FixedWTInv fixedWTInv;

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WCTGuiObject wctGUIObject;

    public WCTContainer(int id, final PlayerInventory ip, final WCTGuiObject gui) {
        super(TYPE, id, ip, gui, true);
        wctGUIObject = gui;

        final int slotIndex = ((IInventorySlotAware) wctGUIObject).getInventorySlot();
        lockPlayerInventorySlot(slotIndex);

        fixedWTInv = new FixedWTInv(getPlayerInv(), wctGUIObject.getItemStack(), this);
        craftingGrid = new ae2wtlibInternalInventory(this, 9, "crafting", wctGUIObject.getItemStack());
        final FixedItemInv crafting = getInventoryByName("crafting");

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                addSlot(craftingSlots[x + y * 3] = new CraftingMatrixSlot(this, crafting, x + y * 3, 37 + x * 18 + 43, -72 + y * 18 - 4));
            }
        }
        AppEngInternalInventory output = new AppEngInternalInventory(this, 1);
        addSlot(outputSlot = new CraftingTermSlot(getPlayerInv().player, getActionSource(), getPowerSource(), gui.getIStorageGrid(), crafting, crafting, output, 131 + 43, -72 + 18 - 4, this));

        addSlot(new AppEngSlot(fixedWTInv, 3, 8, -76) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
            }
        });
        addSlot(new AppEngSlot(fixedWTInv, 2, 8, -58) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE);
            }
        });
        addSlot(new AppEngSlot(fixedWTInv, 1, 8, -40) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE);
            }
        });
        addSlot(new AppEngSlot(fixedWTInv, 0, 8, -22) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE);
            }
        });

        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.OFFHAND, 80, -22) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.TRASH, 98, -22));
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.INFINITY_BOOSTER_CARD, 134, -20));
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.MAGNET_CARD, 152, -20));//TODO fetch texture for card background
    }

    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(!wctGUIObject.rangeCheck()) {
            if(isValidContainer()) {
                getPlayerInv().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInv().player).closeHandledScreen();
            }
            setValidContainer(false);
        } else {
            double powerMultiplier = Config.getPowerMultiplier(wctGUIObject.getRange(), wctGUIObject.isOutOfRange());
            ticks++;
            if(ticks > 10) {
                wctGUIObject.extractAEPower((powerMultiplier) * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
                ticks = 0;
            }

            if(wctGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidContainer()) {
                    getPlayerInv().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                    ((ServerPlayerEntity) getPlayerInv().player).closeHandledScreen();
                }
                setValidContainer(false);
            }
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */

    @Override
    public void onContentChanged(Inventory inventory) {
        final ContainerNull cn = new ContainerNull();
        final CraftingInventory ic = new CraftingInventory(cn, 3, 3);

        for(int x = 0; x < 9; x++) {
            ic.setStack(x, craftingSlots[x].getStack());
        }

        if(currentRecipe == null || !currentRecipe.matches(ic, this.getPlayerInv().player.world)) {
            World world = this.getPlayerInv().player.world;
            currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);
        }

        if(currentRecipe == null) {
            outputSlot.setStack(ItemStack.EMPTY);
        } else {
            final ItemStack craftingResult = currentRecipe.craft(ic);
            outputSlot.setStack(craftingResult);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(FixedItemInv inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {}

    @Override
    public FixedItemInv getInventoryByName(String name) {
        if(name.equals("player")) {
            return new FixedInventoryVanillaWrapper(getPlayerInventory());
        } else if(name.equals("crafting")) {
            return craftingGrid;
        }
        return null;
    }

    @Override
    public IGridNode getNetworkNode() {
        return wctGUIObject.getActionableNode();
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public void deleteTrashSlot() {
        fixedWTInv.setInvStack(FixedWTInv.TRASH, ItemStack.EMPTY, Simulation.ACTION);
    }

    private MagnetSettings magnetSettings;

    public MagnetSettings getMagnetSettings() {
        if(magnetSettings == null) return reloadMagnetSettings();
        return magnetSettings;
    }

    public void saveMagnetSettings() {
        ItemMagnetCard.saveMagnetSettings(wctGUIObject.getItemStack(), magnetSettings);
    }

    public MagnetSettings reloadMagnetSettings() {
        magnetSettings = ItemMagnetCard.loadMagnetSettings(wctGUIObject.getItemStack());
        if(isClient() && screen != null) screen.resetMagnetSettings();
        return magnetSettings;
    }

    @Environment(EnvType.CLIENT)
    private WCTScreen screen;

    @Environment(EnvType.CLIENT)
    public void setScreen(WCTScreen screen) {
        this.screen = screen;
    }

    public boolean isWUT() {
        return wctGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public ItemStack[] getViewCells() {
        return wctGUIObject.getViewCellStorage().getViewCells();
    }
}