package de.mari_023.fabric.ae2wtlib.util;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import net.minecraft.item.ItemStack;

public class FixedEmptyInventory implements FixedItemInv {

    private final int size;

    public FixedEmptyInventory(int size) {
        this.size = size;
    }

    @Override
    public int getSlotCount() {
        return size;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
        return false;
    }
}