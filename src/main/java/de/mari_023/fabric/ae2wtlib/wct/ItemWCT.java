package de.mari_023.fabric.ae2wtlib.wct;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.ItemWT;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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

    @Override
    public boolean hasBoosterCard(ItemStack hostItem) {
        return getBoosterCard(hostItem).getItem() instanceof ItemInfinityBooster;
    }

    @Override
    public void setBoosterCard(ItemStack hostItem, ItemStack boosterCard) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) {
            setSavedSlot(hostItem, boosterCard, "boosterCard");
        }
    }

    @Override
    public ItemStack getBoosterCard(ItemStack hostItem) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) {
            return getSavedSlot(hostItem, "boosterCard");
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getMagnetCard(ItemStack item) {
        if(!(item.getItem() instanceof ItemWCT) || item.getTag() == null) return ItemStack.EMPTY;
        return ItemStack.fromTag(item.getTag().getCompound("magnet_card"));
    }

    public void setMagnetCard(ItemStack item, ItemStack magnet_card) {
        if(!(item.getItem() instanceof ItemWCT)) return;
        CompoundTag wctTag = item.getTag();
        if(magnet_card.isEmpty()) {
            if(wctTag == null) return;
            wctTag.put("magnet_card", ItemStack.EMPTY.toTag(new CompoundTag()));
        } else {
            if(wctTag == null) wctTag = new CompoundTag();
            wctTag.put("magnet_card", magnet_card.toTag(new CompoundTag()));
        }
        item.setTag(wctTag);
    }
}