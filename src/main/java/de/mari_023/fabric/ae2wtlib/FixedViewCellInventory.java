package de.mari_023.fabric.ae2wtlib;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import appeng.items.storage.ViewCellItem;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class FixedViewCellInventory implements FixedItemInv {

    private final ItemStack[] viewCells;

    public FixedViewCellInventory(int size) {
        viewCells = new ItemStack[size];
        Arrays.fill(viewCells, ItemStack.EMPTY);
    }

    @Override
    public int getSlotCount() {
        return viewCells.length;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if(i < viewCells.length) return viewCells[i];
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        return i < viewCells.length && itemStack.getItem() instanceof ViewCellItem;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(Simulation.SIMULATE.isAction()) {
            if(isItemValidForSlot(i, itemStack)) {
                viewCells[i] = itemStack;
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getViewCells() {
        return viewCells;
    }
}