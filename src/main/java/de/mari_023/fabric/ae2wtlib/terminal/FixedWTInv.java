package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.inventories.InternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import org.jetbrains.annotations.NotNull;

public class FixedWTInv implements InternalInventory {

    public static final int OFFHAND = 4;
    public static final int TRASH = 5;
    public static final int INFINITY_BOOSTER_CARD = 6;
    public static final int MAGNET_CARD = 7;

    private final PlayerInventory playerInventory;
    private final ItemStack wt;
    private final IWTInvHolder host;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public FixedWTInv(PlayerInventory playerInventory, ItemStack wt, IWTInvHolder host) {
        this.playerInventory = playerInventory;
        this.wt = wt;
        this.host = host;
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if(i < 4 && i >= 0) return playerInventory.getStack(i + slotOffset);
        else if(i == OFFHAND) return playerInventory.getStack(offHandSlot);
        else if(i == TRASH && wt.getItem() instanceof ItemWT) return ItemWT.getSavedSlot(wt, "trash");
        else if(i == INFINITY_BOOSTER_CARD && wt.getItem() instanceof IInfinityBoosterCardHolder)
            return ((IInfinityBoosterCardHolder) wt.getItem()).getBoosterCard(wt);
        else if(i == MAGNET_CARD && wt.getItem() instanceof ItemWT) return ItemWT.getSavedSlot(wt, "magnetCard");
        return null;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        if(i == 0)
            return playerInventory.isValid(slotOffset, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.FEET);
        else if(i == 1)
            return playerInventory.isValid(slotOffset + 1, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.LEGS);
        else if(i == 2)
            return playerInventory.isValid(slotOffset + 2, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.CHEST);
        else if(i == 3)
            return playerInventory.isValid(slotOffset + 3, itemStack) && ((itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.HEAD))
                    || (itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof Wearable));
        else if(i == OFFHAND) return playerInventory.isValid(offHandSlot, itemStack);
        else if(i == TRASH) return true;
        else if(i == INFINITY_BOOSTER_CARD)
            return itemStack.getItem() instanceof ItemInfinityBooster || itemStack.isEmpty();
        else if(i == MAGNET_CARD)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        if(i < 4 && i >= 0) {
            playerInventory.setStack(i + slotOffset, itemStack);
        } else if(i == OFFHAND) {
            playerInventory.setStack(offHandSlot, itemStack);
        } else if(i == TRASH) {
            ItemWT.setSavedSlot(wt, itemStack, "trash");
        } else if(i == INFINITY_BOOSTER_CARD) {
            if(!(itemStack.getItem() instanceof ItemInfinityBooster) && !itemStack.equals(ItemStack.EMPTY)) return;
            ((IInfinityBoosterCardHolder) wt.getItem()).setBoosterCard(wt, itemStack);
        } else if(i == MAGNET_CARD) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return;
            ItemWT.setSavedSlot(wt, itemStack, "magnetCard");
            if(host instanceof WCTContainer) ((WCTContainer) host).reloadMagnetSettings();
        }
    }

    /*@Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot == INFINITY_BOOSTER_CARD) {
            ItemStack boosterCard = ((IInfinityBoosterCardHolder) wt.getItem()).getBoosterCard(wt);
            if(simulation.isAction()) ((IInfinityBoosterCardHolder) wt.getItem()).setBoosterCard(wt, ItemStack.EMPTY);
            return boosterCard;
        } else if(slot == MAGNET_CARD) {
            ItemStack magnetCard = ItemWT.getSavedSlot(wt, "magnetCard");
            if(simulation.isAction()) {
                ItemWT.setSavedSlot(wt, ItemStack.EMPTY, "magnetCard");
                if(host instanceof WCTContainer) ((WCTContainer) host).reloadMagnetSettings();
            }
            return magnetCard;
        }
        return FixedItemInv.super.extractStack(slot, filter, mergeWith, maxCount, simulation);
    }*/
}