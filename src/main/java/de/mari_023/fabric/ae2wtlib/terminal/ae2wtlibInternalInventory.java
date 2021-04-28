package de.mari_023.fabric.ae2wtlib.terminal;

import alexiil.mc.lib.attributes.Simulation;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.item.ItemStack;

public class ae2wtlibInternalInventory extends AppEngInternalInventory {

    private final ItemStack terminal;
    private final String identifier;

    public ae2wtlibInternalInventory(IAEAppEngInventory inventory, int size, String identifier, ItemStack terminal) {
        super(inventory, size);
        this.terminal = terminal;
        this.identifier = identifier;
        for (int slot = 0; slot < size; slot++)
            super.setInvStack(slot, ItemWT.getSavedSlot(terminal, identifier + slot), Simulation.ACTION);
    }

    @Override
    public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
        boolean value = super.setInvStack(slot, to, simulation);
        if (value && simulation.isAction()) ItemWT.setSavedSlot(terminal, to, identifier + slot);
        return value;
    }
}