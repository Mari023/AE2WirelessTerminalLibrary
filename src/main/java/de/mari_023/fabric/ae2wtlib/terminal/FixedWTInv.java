package de.mari_023.fabric.ae2wtlib.terminal;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import de.mari_023.fabric.ae2wtlib.wct.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class FixedWTInv implements FixedItemInv {

    public static final int OFFHAND = 4;
    public static final int TRASH = 5;
    public static final int INFINITY_BOOSTER_CARD = 6;
    public static final int MAGNET_CARD = 7;


    private final PlayerInventory playerInventory;
    private final ItemStack wct;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public FixedWTInv(PlayerInventory playerInventory, ItemStack wct) {
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
        } else if(i == OFFHAND) return playerInventory.getStack(offHandSlot);
        else if(i == TRASH && wct.getItem() instanceof ItemWCT)
            return ((ItemWCT) wct.getItem()).getSavedSlot(wct, "trash");
        else if(i == INFINITY_BOOSTER_CARD && wct.getItem() instanceof IInfinityBoosterCardHolder)
            return ((IInfinityBoosterCardHolder) wct.getItem()).getBoosterCard(wct);
        else if(i == MAGNET_CARD && wct.getItem() instanceof ItemWCT)
            return ((ItemWCT) wct.getItem()).getSavedSlot(wct, "magnetCard");
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if(i < 4 && i >= 0) {
            return playerInventory.isValid(i + slotOffset, itemStack);
        } else if(i == OFFHAND) return playerInventory.isValid(offHandSlot, itemStack);
        else if(i == TRASH) return true;
        else if(i == INFINITY_BOOSTER_CARD)
            return itemStack.getItem() instanceof ItemInfinityBooster || itemStack.isEmpty();
        else if(i == MAGNET_CARD && wct.getItem() instanceof ItemWCT)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(i < 4 && i >= 0) {
            if(simulation.isAction()) playerInventory.setStack(i + slotOffset, itemStack);
            return true;
        } else if(i == OFFHAND) {
            if(simulation.isAction()) playerInventory.setStack(offHandSlot, itemStack);
            return true;
        } else if(i == TRASH) {
            if(simulation.isAction()) ((ItemWT) wct.getItem()).setSavedSlot(wct, itemStack, "trash");
            return true;
        } else if(i == INFINITY_BOOSTER_CARD) {
            if(!(itemStack.getItem() instanceof ItemInfinityBooster) && !itemStack.equals(ItemStack.EMPTY))
                return false;
            if(simulation.isAction()) {
                ((IInfinityBoosterCardHolder) wct.getItem()).setBoosterCard(wct, itemStack);
            }
            return true;
        } else if(i == MAGNET_CARD) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return false;
            if(simulation.isAction())
                ((ItemWT) wct.getItem()).setSavedSlot(wct, itemStack, "magnetCard");
            return true;
        }
        return false;
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot == INFINITY_BOOSTER_CARD) {
            ItemStack boosterCard = ((IInfinityBoosterCardHolder) wct.getItem()).getBoosterCard(wct);
            if(simulation.isAction()) ((IInfinityBoosterCardHolder) wct.getItem()).setBoosterCard(wct, ItemStack.EMPTY);
            return boosterCard;
        } else if(slot == MAGNET_CARD) {
            ItemStack magnetCard = ((ItemWT) wct.getItem()).getSavedSlot(wct, "magnetCard");
            if(simulation.isAction())
                ((ItemWT) wct.getItem()).setSavedSlot(wct, ItemStack.EMPTY, "magnetCard");
            return magnetCard;
        }
        return FixedItemInv.super.extractStack(slot, filter, mergeWith, maxCount, simulation);
    }
}