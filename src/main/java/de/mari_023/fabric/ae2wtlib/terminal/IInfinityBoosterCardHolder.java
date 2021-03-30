package de.mari_023.fabric.ae2wtlib.terminal;

import net.minecraft.item.ItemStack;

public interface IInfinityBoosterCardHolder {

    boolean hasBoosterCard(ItemStack hostItem);

    void setBoosterCard(ItemStack hostItem, ItemStack boosterCard);

    ItemStack getBoosterCard(ItemStack hostItem);
}