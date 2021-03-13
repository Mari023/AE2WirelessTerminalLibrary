package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.ItemWT;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ItemWPT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWPT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }

    @Override
    public void open(final PlayerEntity player, final Hand hand) {
        WPTContainer.open(player, ContainerLocator.forHand(player, hand));
    }

    @Override
    public boolean canHandle(ItemStack is) {
        return is.getItem() instanceof ItemWPT;
    }

    @Override
    public boolean hasBoosterCard(ItemStack item) {
        return item.getItem() instanceof IInfinityBoosterCardHolder && item.getTag() != null && item.getTag().getBoolean("hasboostercard");
    }

    @Override
    public void setBoosterCard(ItemStack item, boolean hasBoosterCard) {
        if(item.getItem() instanceof IInfinityBoosterCardHolder && item.getTag() != null && hasBoosterCard(item) != hasBoosterCard) {
            item.getTag().putBoolean("hasboostercard", hasBoosterCard);
        }
    }

    @Override
    public ItemStack boosterCard(ItemStack item) {
        if(hasBoosterCard(item)) return new ItemStack(ae2wtlib.INFINITY_BOOSTER);
        return ItemStack.EMPTY;
    }
}