package de.mari_023.fabric.ae2wtlib.wet;

import appeng.core.AEConfig;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.entity.player.Player;

public class ItemWET extends ItemWT {

    public ItemWET() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public boolean open(final Player player, final MenuLocator locator) {
        return MenuOpener.open(WETMenu.TYPE, player, locator);
    }
}