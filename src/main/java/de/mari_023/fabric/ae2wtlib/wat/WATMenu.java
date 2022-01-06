package de.mari_023.fabric.ae2wtlib.wat;

import appeng.api.config.SecurityPermissions;
import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.InterfaceTerminalMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class WATMenu extends InterfaceTerminalMenu {

    public static final MenuType<WATMenu> TYPE = MenuTypeBuilder.create(WATMenu::new, WATMenuHost.class).requirePermission(SecurityPermissions.BUILD).build("wireless_pattern_access_terminal");

    private final WATMenuHost witGUIObject;
    private final ToolboxMenu toolboxMenu;

    public WATMenu(int id, final Inventory ip, final WATMenuHost anchor) {
        super(TYPE, id, ip, anchor, true);
        witGUIObject = anchor;
        toolboxMenu = new ToolboxMenu(this);

        IUpgradeInventory upgrades = witGUIObject.getUpgrades();
        for(int i = 0; i < upgrades.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            addSlot(slot, SlotSemantics.UPGRADE);
        }
    }

    @Override
    public void broadcastChanges() {
        toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    public ITerminalHost getHost() {
        return witGUIObject;
    }

    public ToolboxMenu getToolbox() {
        return toolboxMenu;
    }
}