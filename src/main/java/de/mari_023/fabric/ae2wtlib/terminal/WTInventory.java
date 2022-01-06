package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.inventories.InternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class WTInventory implements InternalInventory {

    public static final int TRASH = 5;
    public static final int MAGNET_CARD = 7;

    private final ItemStack wt;
    private final WCTMenu host;

    public WTInventory(ItemStack wt, WCTMenu host) {
        this.wt = wt;
        this.host = host;
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if(i == TRASH) return ItemWT.getSavedSlot(wt, "trash");
        else if(i == MAGNET_CARD) return ItemWT.getSavedSlot(wt, "magnetCard");
        return null;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        if(i == TRASH) return true;
        else if(i == MAGNET_CARD)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public void setItemDirect(int i, @NotNull ItemStack itemStack) {
        if(i == TRASH) {
            ItemWT.setSavedSlot(wt, itemStack, "trash");
        } else if(i == MAGNET_CARD) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return;
            ItemWT.setSavedSlot(wt, itemStack, "magnetCard");
            host.reloadMagnetSettings();
        }
    }
}