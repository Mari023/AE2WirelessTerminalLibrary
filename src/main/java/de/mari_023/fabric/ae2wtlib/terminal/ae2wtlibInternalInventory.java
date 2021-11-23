package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ae2wtlibInternalInventory extends AppEngInternalInventory {//TODO replace this and save in TerminalHost like anything else reasonable

    private final ItemStack terminal;
    private final String identifier;

    public ae2wtlibInternalInventory(InternalInventoryHost inventory, int size, String identifier, ItemStack terminal) {
        super(inventory, size);
        this.terminal = terminal;
        this.identifier = identifier;
        for(int slot = 0; slot < size; slot++)
            super.setItemDirect(slot, ItemWT.getSavedSlot(terminal, identifier + slot));
    }

    @Override
    public void setItemDirect(int slot, @NotNull ItemStack to) {
        super.setItemDirect(slot, to);
        ItemWT.setSavedSlot(terminal, to, identifier + slot);
    }
}