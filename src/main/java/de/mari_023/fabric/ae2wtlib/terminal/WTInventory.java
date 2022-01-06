package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.inventories.InternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class WTInventory implements InternalInventory {

    private final ItemStack wt;
    private final WCTMenu host;

    public WTInventory(ItemStack wt, WCTMenu host) {
        this.wt = wt;
        this.host = host;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return ItemWT.getSavedSlot(wt, "magnetCard");
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return;
        ItemWT.setSavedSlot(wt, itemStack, "magnetCard");
        host.reloadMagnetSettings();
    }
}