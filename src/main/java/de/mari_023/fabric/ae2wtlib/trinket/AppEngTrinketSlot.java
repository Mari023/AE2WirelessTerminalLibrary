package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.container.slot.AppEngSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AppEngTrinketSlot extends AppEngSlot {

    public boolean keepVisible = false;
    public String group, slot;
    public final boolean locked;

    public AppEngTrinketSlot(FixedTrinketInv inv, int invSlot, int x, int y, String group, String slot, boolean locked) {
        super(inv, invSlot, x, y);
        this.group = group;
        this.slot = slot;
        this.locked = locked;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return !locked && super.canInsert(stack);
    }

    public boolean canTakeItems(PlayerEntity player) {
        return !locked && super.canTakeItems(player);
    }

    public ItemStack takeStack(int amount) {
        return locked ? ItemStack.EMPTY : super.takeStack(amount);
    }
}