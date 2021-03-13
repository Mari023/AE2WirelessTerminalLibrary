package de.mari_023.fabric.ae2wtlib.wct;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import de.mari_023.fabric.ae2wtlib.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.ItemInfinityBooster;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class FixedWCTInv implements FixedItemInv {

    private final PlayerInventory playerInventory;
    private ItemStack trashSlot = ItemStack.EMPTY;
    private final ItemStack wct;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public FixedWCTInv(PlayerInventory playerInventory, ItemStack wct) {
        this.playerInventory = playerInventory;
        this.wct = wct;
    }

    @Override
    public int getSlotCount() {
        return 8;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if(i < 4 && i >= 0) {
            return playerInventory.getStack(i + slotOffset);
        } else if(i == 4) return playerInventory.getStack(offHandSlot);
        else if(i == 5) return trashSlot;
        else if(i == 6 && wct.getItem() instanceof IInfinityBoosterCardHolder)
            return ((IInfinityBoosterCardHolder) wct.getItem()).getBoosterCard(wct);
        else if(i == 7 && wct.getItem() instanceof ItemWCT) return ((ItemWCT) wct.getItem()).getMagnetCard(wct);
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if(i < 4 && i >= 0) {
            return playerInventory.isValid(i + slotOffset, itemStack);
        } else if(i == 4) return playerInventory.isValid(offHandSlot, itemStack);
        else if(i == 5) return true;
        else if(i == 6)
            return (itemStack.getItem() instanceof ItemInfinityBooster || itemStack.equals(ItemStack.EMPTY));
        else if(i == 7 && wct.getItem() instanceof ItemWCT)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(i < 4 && i >= 0) {
            if(simulation.isAction()) playerInventory.setStack(i + slotOffset, itemStack);
            return true;
        } else if(i == 4) {
            if(simulation.isAction()) playerInventory.setStack(offHandSlot, itemStack);
            return true;
        } else if(i == 5) {
            if(simulation.isAction()) trashSlot = itemStack;
            return true;
        } else if(i == 6) {
            if(!(itemStack.getItem() instanceof ItemInfinityBooster) && !itemStack.equals(ItemStack.EMPTY))
                return false;
            if(simulation.isAction()) {
                ((IInfinityBoosterCardHolder) wct.getItem()).setBoosterCard(wct, itemStack);
            }
            return true;
        } else if(i == 7) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return false;
            if(simulation.isAction()) ((ItemWCT) wct.getItem()).setMagnetCard(wct, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot == 6) {
            ItemStack boosterCard = ((IInfinityBoosterCardHolder) wct.getItem()).getBoosterCard(wct);
            if(simulation.isAction()) ((IInfinityBoosterCardHolder) wct.getItem()).setBoosterCard(wct, ItemStack.EMPTY);
            return boosterCard;
        } else if(slot == 7) {
            ItemStack magnetCard = ((ItemWCT) wct.getItem()).getMagnetCard(wct);
            if(simulation.isAction()) ((ItemWCT) wct.getItem()).setMagnetCard(wct, ItemStack.EMPTY);
            return magnetCard;
        }
        return FixedItemInv.super.extractStack(slot, filter, mergeWith, maxCount, simulation);
    }
}