package de.mari_023.fabric.ae2wtlib.trinket;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.item.ItemStack;

public class FixedTrinketInv implements FixedItemInv.ModifiableFixedItemInv {

    private final TrinketInventory inventory;

    public FixedTrinketInv(TrinketInventory trinketInventory) {
        inventory = trinketInventory;
    }

    @Override
    public int getSlotCount() {
        return inventory.size();
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return inventory.isValid(slot, stack);
    }

    @Override
    public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
        boolean valid = isItemValidForSlot(slot, to);
        if(valid && Simulation.ACTION.isAction()) inventory.setStack(slot, to);
        return valid;
    }

    @Override
    public void markDirty() {
        inventory.markDirty();
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(mergeWith.isEmpty()) {
            ItemStack stack = inventory.getStack(slot);
            if(Simulation.SIMULATE.isAction()) {
                inventory.removeStack(slot);
                markDirty();
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }
}