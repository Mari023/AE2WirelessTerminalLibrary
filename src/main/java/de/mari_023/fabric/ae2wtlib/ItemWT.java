package de.mari_023.fabric.ae2wtlib;

import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.DoubleSupplier;

public abstract class ItemWT extends AEBasePoweredItem implements ICustomWirelessTerminalItem {

    public ItemWT(DoubleSupplier powerCapacity, Settings props) {
        super(powerCapacity, props);
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     * @param hostItem the Terminal to load from
     * @param slot the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    public ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if(!(hostItem.getItem() instanceof ItemWT) || hostItem.getTag() == null) return ItemStack.EMPTY;
        return ItemStack.fromTag(hostItem.getTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     * @param hostItem the Terminal to store in
     * @param savedItem the item to store
     * @param slot the location where the stored item will be
     */
    public void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if(!(hostItem.getItem() instanceof ItemWT)) return;
        CompoundTag wctTag = hostItem.getTag();
        if(savedItem.isEmpty()) {
            if(wctTag == null) return;
            wctTag.put(slot, ItemStack.EMPTY.toTag(new CompoundTag()));
        } else {
            if(wctTag == null) wctTag = new CompoundTag();
            wctTag.put(slot, savedItem.toTag(new CompoundTag()));
        }
        hostItem.setTag(wctTag);
    }
}