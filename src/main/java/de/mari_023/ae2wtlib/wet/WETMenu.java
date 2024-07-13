package de.mari_023.ae2wtlib.wet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.networking.IGridNode;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;

import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

import static de.mari_023.ae2wtlib.api.AE2wtlibAPI.id;

public class WETMenu extends PatternEncodingTermMenu {
    public static final ResourceLocation ID = id("wireless_pattern_encoding_terminal");
    public static final MenuType<WETMenu> TYPE = MenuTypeBuilder.create(WETMenu::new, WETMenuHost.class).build(ID);

    private final WETMenuHost wetMenuHost;

    public WETMenu(int id, final Inventory ip, final WETMenuHost gui) {
        super(TYPE, id, ip, gui, true);
        wetMenuHost = gui;
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                wetMenuHost.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public IGridNode getGridNode() {
        return wetMenuHost.getActionableNode();
    }

    public boolean isWUT() {
        return wetMenuHost.getItemStack().getItem() instanceof ItemWUT;
    }
}
