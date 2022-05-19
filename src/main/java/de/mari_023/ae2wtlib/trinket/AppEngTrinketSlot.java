package de.mari_023.ae2wtlib.trinket;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import dev.emi.trinkets.TrinketsClient;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;

import appeng.menu.slot.AppEngSlot;

public class AppEngTrinketSlot extends AppEngSlot {
    private final SlotGroup group;
    private final SlotType type;
    private final boolean alwaysVisible;
    private final int slotOffset;
    public final TrinketInventoryWrapper trinketInventory;
    public final boolean locked;

    public AppEngTrinketSlot(TrinketInventoryWrapper inventory, int invSlot, SlotGroup group, SlotType type,
            int slotOffset, boolean alwaysVisible, boolean locked) {
        super(inventory, invSlot);
        this.group = group;
        this.type = type;
        this.alwaysVisible = alwaysVisible;
        this.slotOffset = slotOffset;
        this.trinketInventory = inventory;
        this.locked = locked;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !locked && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !locked && super.mayPickup(player);
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return locked ? ItemStack.EMPTY : super.remove(amount);
    }

    @Override
    public boolean isActive() {
        return alwaysVisible || isTrinketFocused();
    }

    public boolean isTrinketFocused() {
        if (TrinketsClient.activeGroup == group) {
            return slotOffset == 0 || TrinketsClient.activeType == type;
        } else if (TrinketsClient.quickMoveGroup == group) {
            return slotOffset == 0 || TrinketsClient.quickMoveType == type && TrinketsClient.quickMoveTimer > 0;
        }
        return false;
    }

    public SlotType getType() {
        return type;
    }

    public ResourceLocation getBackgroundIdentifier() {
        return type.getIcon();
    }
}
