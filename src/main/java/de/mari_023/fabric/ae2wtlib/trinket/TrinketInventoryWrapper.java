package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.api.inventories.InternalInventory;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TrinketInventoryWrapper implements InternalInventory {

    public final TrinketInventory trinketInventory;

    public TrinketInventoryWrapper(TrinketInventory trinketInventory) {
        this.trinketInventory = trinketInventory;
    }

    @Override
    public int size() {
        return trinketInventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return trinketInventory.getStack(i);
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        trinketInventory.setStack(i, itemStack);
    }
}
