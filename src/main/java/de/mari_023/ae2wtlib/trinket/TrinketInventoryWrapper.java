package de.mari_023.ae2wtlib.trinket;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

import dev.emi.trinkets.api.TrinketInventory;

import appeng.api.inventories.InternalInventory;

public class TrinketInventoryWrapper implements InternalInventory {

    public final TrinketInventory trinketInventory;

    public TrinketInventoryWrapper(TrinketInventory trinketInventory) {
        this.trinketInventory = trinketInventory;
    }

    @Override
    public int size() {
        return trinketInventory.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return trinketInventory.getItem(i);
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        trinketInventory.setItem(i, itemStack);
    }
}
