package de.mari_023.fabric.ae2wtlib.wct;

import alexiil.mc.lib.attributes.Simulation;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import de.mari_023.fabric.ae2wtlib.ItemWT;
import net.minecraft.item.ItemStack;

public class CraftingInv extends AppEngInternalInventory {

    private final ItemStack terminal;

    public CraftingInv(IAEAppEngInventory inventory, ItemStack terminal) {
        super(inventory, 9);
        this.terminal = terminal;
        for(int slot = 0; slot < 9; slot++)
            super.setInvStack(slot, ((ItemWCT) terminal.getItem()).getSavedSlot(terminal, "crafting" + slot), Simulation.ACTION);
    }

    @Override
    public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
        boolean value = super.setInvStack(slot, to, simulation);
        if(value && simulation.isAction()) ((ItemWT) terminal.getItem()).setSavedSlot(terminal, to, "crafting" + slot);
        return value;
    }
}