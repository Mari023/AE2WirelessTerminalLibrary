package de.mari_023.fabric.ae2wtlib;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class FixedViewCellInventory implements FixedItemInv {//FIXME viewcells

    private final ItemStack[] viewCells;

    public FixedViewCellInventory() {
        viewCells = new ItemStack[5];
        Arrays.fill(viewCells, ItemStack.EMPTY);
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public ItemStack getInvStack(int i) {
        /*if(i < 5)*/
        return viewCells[i];
        //return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        //viewCells[i] = itemStack;
        return true;
        //return i < viewCells.length && itemStack.getItem() instanceof ViewCellItem;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {//somehow itemStack is always the item that is already in here
        if(simulation.isAction()) {
            //if(isItemValidForSlot(i, itemStack)) {
            viewCells[i] = itemStack;
            //return true;
            //}
        }
        return true;
    }

    public ItemStack[] getViewCells() {
        return viewCells.clone();
    }
}