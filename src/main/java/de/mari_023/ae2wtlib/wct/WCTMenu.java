package de.mari_023.ae2wtlib.wct;

import java.util.Objects;

import appeng.menu.SlotSemantic;
import appeng.menu.slot.RestrictedInputSlot;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.terminal.ArmorSlot;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wut.ItemWUT;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.item.Wearable;

public class WCTMenu extends CraftingTermMenu {

    public static final MenuType<WCTMenu> TYPE = MenuTypeBuilder.create(WCTMenu::new, WCTMenuHost.class)
            .requirePermission(SecurityPermissions.CRAFT).build("wireless_crafting_terminal");

    public static final String ACTION_DELETE = "delete";
    public static final String MAGNET_MODE = "magnetMode";
    public static final String MAGNET_MENU = "magnetMenu";

    private final WCTMenuHost wctMenuHost;

    public WCTMenu(int id, final Inventory ip, final WCTMenuHost gui) {
        super(TYPE, id, ip, gui, true);
        wctMenuHost = gui;

        addSlot(new ArmorSlot(getPlayerInventory(), ArmorSlot.Armor.HEAD) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return (stack.getItem() instanceof BlockItem bItem && bItem.getBlock() instanceof Wearable) || super.mayPlace(stack);
            }
        }, AE2wtlibSlotSemantics.HELMET);
        addSlot(new ArmorSlot(getPlayerInventory(), ArmorSlot.Armor.CHEST), AE2wtlibSlotSemantics.CHESTPLATE);
        addSlot(new ArmorSlot(getPlayerInventory(), ArmorSlot.Armor.LEGS), AE2wtlibSlotSemantics.LEGGINGS);
        addSlot(new ArmorSlot(getPlayerInventory(), ArmorSlot.Armor.FEET), AE2wtlibSlotSemantics.BOOTS);

        if (Integer.valueOf(Inventory.SLOT_OFFHAND).equals(wctMenuHost.getSlot()))
            addSlot(new ArmorSlot.DisabledOffhandSlot(getPlayerInventory()), AE2wtlibSlotSemantics.OFFHAND);
        else
            addSlot(new ArmorSlot(getPlayerInventory(), ArmorSlot.Armor.OFFHAND), AE2wtlibSlotSemantics.OFFHAND);
        addSlot(new AppEngSlot(wctMenuHost.getSubInventory(WCTMenuHost.INV_TRASH), 0), AE2wtlibSlotSemantics.TRASH);
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY, wctMenuHost.getSubInventory(WCTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);

        registerClientAction(ACTION_DELETE, this::deleteTrashSlot);
        registerClientAction(MAGNET_MODE, MagnetMode.class, this::setMagnetMode);
        registerClientAction(MAGNET_MENU, this::openMagnetMenu);
    }

    @Override
    public IGridNode getNetworkNode() {
        return wctMenuHost.getActionableNode();
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public void deleteTrashSlot() {
        if (isClientSide())
            sendClientAction(ACTION_DELETE);
        Objects.requireNonNull(wctMenuHost.getSubInventory(WCTMenuHost.INV_TRASH)).setItemDirect(0, ItemStack.EMPTY);
    }

    private MagnetSettings magnetSettings;

    public MagnetSettings getMagnetSettings() {
        return magnetSettings = MagnetHandler.getMagnetSettings(wctMenuHost.getItemStack());
    }

    public void saveMagnetSettings() {
        MagnetHandler.saveMagnetSettings(wctMenuHost.getItemStack(), magnetSettings);
    }

    public void setMagnetMode(MagnetMode mode) {
        if (isClientSide())
            sendClientAction(MAGNET_MODE, mode);
        getMagnetSettings().magnetMode = mode;
        saveMagnetSettings();
    }

    public void openMagnetMenu() {
        if (isClientSide()) {
            sendClientAction(MAGNET_MENU);
            return;
        }
        MenuOpener.open(MagnetMenu.TYPE, getPlayer(), getLocator());
    }

    @Override
    protected boolean canSlotsBeHidden(SlotSemantic semantic) {
        return semantic == AE2wtlibSlotSemantics.OFFHAND
                || semantic == AE2wtlibSlotSemantics.HELMET
                || semantic == AE2wtlibSlotSemantics.CHESTPLATE
                || semantic == AE2wtlibSlotSemantics.LEGGINGS
                || semantic == AE2wtlibSlotSemantics.BOOTS
                || semantic == AE2wtlibSlotSemantics.TRASH;
    }

    public boolean isWUT() {
        return wctMenuHost.getItemStack().getItem() instanceof ItemWUT;
    }
}
