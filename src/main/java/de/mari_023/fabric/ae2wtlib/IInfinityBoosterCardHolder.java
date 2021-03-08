package de.mari_023.fabric.ae2wtlib;

import net.minecraft.item.ItemStack;

public interface IInfinityBoosterCardHolder {

    boolean hasBoosterCard(ItemStack item);

    void setBoosterCard(ItemStack item, boolean hasBoosterCard);

    ItemStack boosterCard(ItemStack item);
}