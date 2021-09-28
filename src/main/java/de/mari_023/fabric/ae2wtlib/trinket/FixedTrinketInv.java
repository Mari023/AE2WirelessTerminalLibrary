package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.api.inventories.InternalInventory;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.item.ItemStack;

public class FixedTrinketInv implements InternalInventory {

    private final TrinketInventory inventory;

    public FixedTrinketInv(TrinketInventory trinketInventory) {
        inventory = trinketInventory;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return inventory.isValid(slot, stack);
    }

    @Override
    public void setItemDirect(int slot, ItemStack to) {
        inventory.setStack(slot, to);
    }

    /*@Override
    public void markDirty() {
        inventory.markDirty();
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(mergeWith.isEmpty()) {
            if(simulation.isAction()) return inventory.removeStack(slot);
            return inventory.getStack(slot);
        }
        return ItemStack.EMPTY;
    }*/
}