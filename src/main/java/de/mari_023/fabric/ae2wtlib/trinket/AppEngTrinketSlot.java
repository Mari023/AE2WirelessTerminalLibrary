package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.menu.slot.AppEngSlot;
import dev.emi.trinkets.TrinketsClient;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class AppEngTrinketSlot extends AppEngSlot {
    private final SlotGroup group;
    private final SlotType type;
    private final boolean alwaysVisible;
    private final int slotOffset;
    public final TrinketInventoryWrapper trinketInventory;
    public final boolean locked;

    public AppEngTrinketSlot(TrinketInventoryWrapper inventory, int invSlot, SlotGroup group, SlotType type, int slotOffset, boolean alwaysVisible, boolean locked) {
        super(inventory, invSlot);
        this.group = group;
        this.type = type;
        this.alwaysVisible = alwaysVisible;
        this.slotOffset = slotOffset;
        this.trinketInventory = inventory;
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

    @Override
    public boolean isEnabled() {
        return alwaysVisible || isTrinketFocused();
    }

    public boolean isTrinketFocused() {
        if(TrinketsClient.activeGroup == group) {
            return slotOffset == 0 || TrinketsClient.activeType == type;
        } else if(TrinketsClient.quickMoveGroup == group) {
            return slotOffset == 0 || TrinketsClient.quickMoveType == type && TrinketsClient.quickMoveTimer > 0;
        }
        return false;
    }

    public SlotType getType() {
        return type;
    }

    public Identifier getBackgroundIdentifier() {
        return type.getIcon();
    }
}