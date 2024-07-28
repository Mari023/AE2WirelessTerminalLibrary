package de.mari_023.ae2wtlib.wct;

import static de.mari_023.ae2wtlib.api.AE2wtlibAPI.id;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;

import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;

public class TrashMenu extends AEBaseMenu implements ISubMenu {
    public static final ResourceLocation ID = id("trash");
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
