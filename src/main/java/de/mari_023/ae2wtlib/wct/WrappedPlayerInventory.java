package de.mari_023.ae2wtlib.wct;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;

import appeng.api.inventories.InternalInventory;

/**
 * PlayerInternalInventory returns the wrong size, so it doesn't work for the armor and offhand (what we actually care
 * about)
 * 
 * @param playerInventory the Inventory to wrap
 */
public record WrappedPlayerInventory(Inventory playerInventory) implements InternalInventory {
    @Override
    public ResourceHandler<ItemResource> toResourceHandler() {
        return PlayerInventoryWrapper.of(playerInventory);
    }

    @Override
    public int size() {
        return playerInventory.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return switch (slotIndex) {
            case 36, 37, 38, 39, Inventory.SLOT_OFFHAND -> playerInventory.getItem(slotIndex);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setItemDirect(int slotIndex, ItemStack stack) {
        playerInventory.setItem(slotIndex, stack);
    }
}
