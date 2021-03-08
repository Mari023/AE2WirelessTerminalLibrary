package de.mari_023.fabric.ae2wtlib;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class FixedWUTInv implements FixedItemInv {

    private final PlayerInventory playerInventory;
    private ItemStack trashSlot = ItemStack.EMPTY;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public FixedWUTInv(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    @Override
    public int getSlotCount() {
        return 6;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if(i < 4 && i >= 0) {
            return playerInventory.getStack(i+slotOffset);
        } else if(i == 4) return playerInventory.getStack(offHandSlot);
        else if(i == 5) return trashSlot;
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if(i < 4 && i >= 0) {
            return playerInventory.isValid(i+slotOffset, itemStack);
        } else if(i == 4) return playerInventory.isValid(offHandSlot, itemStack);
        else if(i == 5) return true;
        return false;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(i < 4 && i >= 0) {
            if(simulation.isAction()) playerInventory.setStack(i+slotOffset, itemStack);
            return true;
        } else if(i == 4) {
            if(simulation.isAction()) playerInventory.setStack(offHandSlot, itemStack);
            return true;
        } else if(i == 5) {
            if(simulation.isAction()) trashSlot = itemStack;
            return true;
        }
        return false;
    }
}