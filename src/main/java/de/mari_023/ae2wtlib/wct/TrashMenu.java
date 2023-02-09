package de.mari_023.ae2wtlib.wct;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;

public class TrashMenu extends AEBaseMenu implements ISubMenu {
    public static final String ID = "trash";
    public static final MenuType<TrashMenu> TYPE = MenuTypeBuilder.create(TrashMenu::new, WCTMenuHost.class).build(ID);

    private final WCTMenuHost host;

    public TrashMenu(int id, Inventory playerInventory, WCTMenuHost host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;

        var trash = host.getSubInventory(WCTMenuHost.INV_TRASH);
        if (trash == null)
            return;
        for (int i = 0; i < trash.size(); i++) {
            addSlot(new AppEngSlot(trash, i), AE2wtlibSlotSemantics.TRASH);
        }
        createPlayerInventorySlots(playerInventory);
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }
}
