package de.mari_023.fabric.ae2wtlib.wat;

import appeng.api.config.SecurityPermissions;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.InterfaceTerminalMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import de.mari_023.fabric.ae2wtlib.terminal.WTInventory;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class WATMenu extends InterfaceTerminalMenu implements IWTInvHolder {

    public static final MenuType<WATMenu> TYPE = MenuTypeBuilder.create(WATMenu::new, WATMenuHost.class).requirePermission(SecurityPermissions.BUILD).build("wireless_pattern_access_terminal");

    private final WATMenuHost witGUIObject;

    public WATMenu(int id, final Inventory ip, final WATMenuHost anchor) {
        super(TYPE, id, ip, anchor, true);
        witGUIObject = anchor;

        final WTInventory fixedWITInv = new WTInventory(getPlayerInventory(), witGUIObject.getItemStack(), this);
        addSlot(new AppEngSlot(fixedWITInv, WTInventory.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);
    }

    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}