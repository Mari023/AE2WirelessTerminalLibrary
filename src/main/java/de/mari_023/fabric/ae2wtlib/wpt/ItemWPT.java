package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.container.ContainerLocator;
import appeng.container.ContainerOpener;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;

public class ItemWPT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWPT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final ContainerLocator locator) {
        ContainerOpener.openContainer(WPTContainer.TYPE, player, locator);
    }
}