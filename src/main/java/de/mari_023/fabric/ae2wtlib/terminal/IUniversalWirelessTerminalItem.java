package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.menu.MenuLocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IUniversalWirelessTerminalItem extends IInfinityBoosterCardHolder {
    boolean open(final PlayerEntity player, final MenuLocator locator);

    default boolean canOpen(ItemStack item, PlayerEntity player) {
        return !item.isEmpty() && checkPreconditions(item, player);
    }

    default boolean tryOpen(PlayerEntity player, MenuLocator locator, ItemStack stack) {
        if(canOpen(stack, player)) return open(player, locator);
        return false;
    }

    boolean checkPreconditions(ItemStack item, PlayerEntity player);

    default boolean hasBoosterCard(ItemStack hostItem) {
        return getBoosterCard(hostItem).getItem() instanceof ItemInfinityBooster;
    }

    default void setBoosterCard(ItemStack hostItem, ItemStack boosterCard) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) ItemWT.setSavedSlot(hostItem, boosterCard, "boosterCard");
    }

    default ItemStack getBoosterCard(ItemStack hostItem) {
        if(hostItem.getItem() instanceof IInfinityBoosterCardHolder) return ItemWT.getSavedSlot(hostItem, "boosterCard");
        return ItemStack.EMPTY;
    }
}
