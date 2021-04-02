package de.mari_023.fabric.ae2wtlib.wut;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ItemWUT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWUT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final Hand hand) {
        ContainerLocator.forHand(player, hand);
        //WCTContainer.open(player, ContainerLocator.forHand(player, hand));
    }
}