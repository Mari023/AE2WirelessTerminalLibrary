package de.mari_023.fabric.ae2wtlib.wct;

import appeng.menu.MenuLocator;
import appeng.menu.MenuOpener;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;

@Deprecated
public class ItemWCT extends ItemWT {

    public ItemWCT() {
        super(new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final MenuLocator locator) {
        MenuOpener.open(WCTContainer.TYPE, player, locator);
    }
}