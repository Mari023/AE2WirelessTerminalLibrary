package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.blockentity.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ae2wtlibInternalInventory extends AppEngInternalInventory {

    private final ItemStack terminal;
    private final String identifier;

    public ae2wtlibInternalInventory(IAEAppEngInventory inventory, int size, String identifier, ItemStack terminal) {
        super(inventory, size);
        this.terminal = terminal;
        this.identifier = identifier;
        for(int slot = 0; slot < size; slot++)
            super.setStackInSlot(slot, ItemWT.getSavedSlot(terminal, identifier + slot));
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack to) {
        super.setStackInSlot(slot, to);
        ItemWT.setSavedSlot(terminal, to, identifier + slot);
    }
}