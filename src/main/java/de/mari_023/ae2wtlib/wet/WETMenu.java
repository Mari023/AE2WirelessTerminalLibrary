package de.mari_023.ae2wtlib.wet;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.networking.IGridNode;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;

public class WETMenu extends PatternEncodingTermMenu {
    public static final String ID = "wireless_pattern_encoding_terminal";
    public static final MenuType<WETMenu> TYPE = MenuTypeBuilder.create(WETMenu::new, WETMenuHost.class).build(ID);

    private final WETMenuHost wetMenuHost;

    public WETMenu(int id, final Inventory ip, final WETMenuHost gui) {
        super(TYPE, id, ip, gui, true);
        wetMenuHost = gui;
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                wetMenuHost.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public IGridNode getNetworkNode() {
        return wetMenuHost.getActionableNode();
    }

    public boolean isWUT() {
        return wetMenuHost.getItemStack().getItem() instanceof ItemWUT;
    }
}
