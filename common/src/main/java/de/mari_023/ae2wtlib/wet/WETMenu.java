package de.mari_023.ae2wtlib.wet;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.wut.ItemWUT;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternEncodingTermMenu;

public class WETMenu extends PatternEncodingTermMenu {

    public static final MenuType<WETMenu> TYPE = MenuTypeBuilder.create(WETMenu::new, WETMenuHost.class)
            .requirePermission(SecurityPermissions.CRAFT).build("wireless_pattern_encoding_terminal");

    private final WETMenuHost WETGUIObject;

    public WETMenu(int id, final Inventory ip, final WETMenuHost gui) {
        super(TYPE, id, ip, gui, true);
        WETGUIObject = gui;
    }

    @Override
    public IGridNode getNetworkNode() {
        return WETGUIObject.getActionableNode();
    }

    public boolean isWUT() {
        return WETGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}
