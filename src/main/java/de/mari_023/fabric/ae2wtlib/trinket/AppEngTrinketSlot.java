package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.menu.slot.AppEngSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AppEngTrinketSlot extends AppEngSlot {

    public boolean keepVisible = false;
    public final String group;
    public final String slot;
    public final boolean locked;

    public AppEngTrinketSlot(TrinketInventoryWrapper inv, int invSlot, String group, String slot, boolean locked) {
        super(inv, invSlot);
        this.group = group;
        this.slot = slot;
        this.locked = locked;
    }

    @Override
    public boolean canInsert(@NotNull ItemStack stack) {
        return !locked && super.canInsert(stack);
    }

    public boolean canTakeItems(PlayerEntity player) {
        return !locked && super.canTakeItems(player);
    }

    public @NotNull ItemStack takeStack(int amount) {
        return locked ? ItemStack.EMPTY : super.takeStack(amount);
    }
}