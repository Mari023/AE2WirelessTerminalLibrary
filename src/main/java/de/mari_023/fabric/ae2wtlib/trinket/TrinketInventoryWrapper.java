package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.api.inventories.InternalInventory;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TrinketInventoryWrapper implements InternalInventory {//TODO use TrinketsApi.getPlayerSlots()

    private final Map<String, Map<String, TrinketInventory>> inventory;

    public TrinketInventoryWrapper(Map<String, Map<String, TrinketInventory>> trinketInventory) {
        inventory = trinketInventory;
    }

    @Override
    public int size() {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                currentIndex += slot.getValue().size();
            }
        }
        return currentIndex;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index < currentIndex + slot.getValue().size()) {
                    currentIndex += slot.getValue().size();
                } else {
                    return slot.getValue().getStack(index - currentIndex);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int index, ItemStack stack) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index < currentIndex + slot.getValue().size()) {
                    currentIndex += slot.getValue().size();
                } else {
                    return slot.getValue().isValid(index - currentIndex, stack);
                }
            }
        }
        return false;
    }

    @Override
    public void setItemDirect(int index, @NotNull ItemStack stack) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index < currentIndex + slot.getValue().size()) {
                    currentIndex += slot.getValue().size();
                } else {
                    slot.getValue().setStack(index - currentIndex, stack);
                    return;
                }
            }
        }
    }

    /*@Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(mergeWith.isEmpty()) {
            if(simulation.isAction()) return inventory.removeStack(slot);
            return inventory.getStack(slot);
        }
        return ItemStack.EMPTY;
    }*/
}