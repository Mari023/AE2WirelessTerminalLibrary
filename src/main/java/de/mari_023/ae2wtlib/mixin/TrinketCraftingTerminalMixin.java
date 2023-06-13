package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;

import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;

@Mixin(value = WirelessCraftingTerminalItem.class, remap = false)
public class TrinketCraftingTerminalMixin extends WirelessTerminalItem implements Trinket {

    public TrinketCraftingTerminalMixin() {
        super(null, null);
    }

    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        inventoryTick(stack, entity.level(), entity, 0, false);
    }
}
