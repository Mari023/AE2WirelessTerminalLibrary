package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.inventories.InternalInventory;
import appeng.items.storage.ViewCellItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FixedViewCellInventory implements InternalInventory {//TODO maybe remove this? there is disable viewcell somewhere

    private static final int viewCellCount = 5;
    private final ItemStack hostStack;

    public FixedViewCellInventory(ItemStack host) {
        hostStack = host;
    }

    @Override
    public int size() {
        return viewCellCount;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return ItemWT.getSavedSlot(hostStack, "viewCell" + i);
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        ItemWT.setSavedSlot(hostStack, itemStack, "viewCell" + i);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        return slot < viewCellCount && (itemStack.getItem() instanceof ViewCellItem || itemStack.isEmpty());
    }

    public List<ItemStack> getViewCells() {
        List<ItemStack> viewCells = new ArrayList<>();
        for(int i = 0; i < viewCellCount; i++) viewCells.add(getStackInSlot(i));
        return viewCells;
    }

    /*@Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot > viewCellCount || !mergeWith.isEmpty()) return ItemStack.EMPTY;
        ItemStack is = getInvStack(slot);
        if(simulation.isAction()) setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
        return is;
    }*/
}