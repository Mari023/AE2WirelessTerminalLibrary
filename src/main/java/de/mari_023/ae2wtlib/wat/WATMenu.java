package de.mari_023.ae2wtlib.wat;

import static de.mari_023.ae2wtlib.api.AE2wtlibAPI.id;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternAccessTermMenu;
import appeng.menu.slot.RestrictedInputSlot;

import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class WATMenu extends PatternAccessTermMenu {
    public static final ResourceLocation ID = id("wireless_pattern_access_terminal");
    public static final MenuType<WATMenu> TYPE = MenuTypeBuilder.create(WATMenu::new, WATMenuHost.class)
            .buildUnregistered(ID);

    private final WATMenuHost watMenuHost;
    private final ToolboxMenu toolboxMenu;

    public WATMenu(int id, final Inventory ip, final WATMenuHost anchor) {
        super(TYPE, id, ip, anchor, true);
        watMenuHost = anchor;
        toolboxMenu = new ToolboxMenu(this);

        IUpgradeInventory upgrades = watMenuHost.getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            addSlot(slot, SlotSemantics.UPGRADE);
        }
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                watMenuHost.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public void broadcastChanges() {
        toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return watMenuHost.getItemStack().getItem() instanceof ItemWUT;
    }

    public ITerminalHost getHost() {
        return watMenuHost;
    }

    public ToolboxMenu getToolbox() {
        return toolboxMenu;
    }
}
