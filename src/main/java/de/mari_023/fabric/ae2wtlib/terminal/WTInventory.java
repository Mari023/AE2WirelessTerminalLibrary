package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.inventories.InternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import org.jetbrains.annotations.NotNull;

public class WTInventory implements InternalInventory {

    public static final int OFF_HAND = 4;
    @Deprecated
    public static final int TRASH = 5;
    @Deprecated
    public static final int MAGNET_CARD = 7;
    private static final int SLOT_OFFSET = 36;

    private final Inventory playerInventory;
    private final ItemStack wt;
    private final WCTMenu host;

    public WTInventory(Inventory playerInventory, ItemStack wt, WCTMenu host) {
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
        if(i < 4 && i >= 0) return playerInventory.getItem(i + SLOT_OFFSET);
        else if(i == OFF_HAND) return playerInventory.getItem(Inventory.SLOT_OFFHAND);
        else if(i == TRASH) return ItemWT.getSavedSlot(wt, "trash");
        else if(i == MAGNET_CARD) return ItemWT.getSavedSlot(wt, "magnetCard");
        return null;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        if(i == 0)
            return playerInventory.canPlaceItem(SLOT_OFFSET, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlot().equals(EquipmentSlot.FEET);
        else if(i == 1)
            return playerInventory.canPlaceItem(SLOT_OFFSET + 1, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlot().equals(EquipmentSlot.LEGS);
        else if(i == 2)
            return playerInventory.canPlaceItem(SLOT_OFFSET + 2, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlot().equals(EquipmentSlot.CHEST);
        else if(i == 3)
            return playerInventory.canPlaceItem(SLOT_OFFSET + 3, itemStack) && ((itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlot().equals(EquipmentSlot.HEAD))
                    || (itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof Wearable));
        else if(i == Inventory.SLOT_OFFHAND)
            return playerInventory.canPlaceItem(Inventory.SLOT_OFFHAND, itemStack);
        else if(i == TRASH) return true;
        else if(i == MAGNET_CARD)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        if(i < 4 && i >= 0) {
            playerInventory.setItem(i + SLOT_OFFSET, itemStack);
        } else if(i == OFF_HAND) {
            playerInventory.setItem(Inventory.SLOT_OFFHAND, itemStack);
        } else if(i == TRASH) {
            ItemWT.setSavedSlot(wt, itemStack, "trash");
        } else if(i == MAGNET_CARD) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return;
            ItemWT.setSavedSlot(wt, itemStack, "magnetCard");
            host.reloadMagnetSettings();
        }
    }
}