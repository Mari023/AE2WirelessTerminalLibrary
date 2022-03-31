package de.mari_023.ae2wtlib.wct.magnet_card.config;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.slot.FakeSlot;
import appeng.util.ConfigInventory;
import appeng.util.ConfigMenuInventory;

public class MagnetMenu extends AEBaseMenu {

    public MagnetMenu(MenuType<?> menuType, int id, Inventory playerInventory, WCTMenuHost host) {
        super(menuType, id, playerInventory, host);

        MagnetHost magnetHost = CraftingTerminalHandler.getCraftingTerminalHandler(playerInventory.player)
                .getMagnetHost();

        if (magnetHost == null)
            return;

        addConfigSlots(magnetHost.pickupConfig, AE2wtlibSlotSemantics.PICKUP_CONFIG);
        addConfigSlots(magnetHost.insertConfig, AE2wtlibSlotSemantics.INSERT_CONFIG);
    }

    private void addConfigSlots(ConfigInventory config, SlotSemantic slotSemantic) {
        ConfigMenuInventory inv = config.createMenuWrapper();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new FakeSlot(inv, y * 3 + x), slotSemantic);
            }
        }
    }
}
