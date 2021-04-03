package de.mari_023.fabric.ae2wtlib.wct;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemWCT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWCT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final Hand hand) {
        WCTContainer.open(player, ContainerLocator.forHand(player, hand));
    }

    @Override
    public boolean canHandle(ItemStack is) {
        return is.getItem() instanceof ItemWCT;
    }
}