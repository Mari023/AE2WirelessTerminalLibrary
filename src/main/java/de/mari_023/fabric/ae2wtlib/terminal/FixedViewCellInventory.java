package de.mari_023.fabric.ae2wtlib.terminal;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import appeng.items.storage.ViewCellItem;
import net.minecraft.item.ItemStack;

public class FixedViewCellInventory implements FixedItemInv {

    private static final int viewCellCount = 5;
    private final ItemStack hostStack;

    public FixedViewCellInventory(ItemStack host) {
        hostStack = host;
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if(i < viewCellCount) return ItemWT.getSavedSlot(hostStack, "viewCell" + i);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        return i < viewCellCount && (itemStack.getItem() instanceof ViewCellItem || itemStack.isEmpty());
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(isItemValidForSlot(i, itemStack)) {
            if(simulation.isAction()) {
                ItemWT.setSavedSlot(hostStack, itemStack, "viewCell" + i);
                return true;
            }
        }
        return true;
    }

    public ItemStack[] getViewCells() {
        ItemStack[] viewCells = new ItemStack[viewCellCount];
        for (int i = 0; i < viewCellCount; i++) {
            viewCells[i] = getInvStack(i);
        }
        return viewCells;
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot > viewCellCount || !mergeWith.isEmpty()) return ItemStack.EMPTY;
        ItemStack is = getInvStack(slot);
        if(simulation.isAction()) setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
        return is;
    }
}