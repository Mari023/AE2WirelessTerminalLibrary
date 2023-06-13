package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;

@Mixin(value = ItemWUT.class, remap = false)
public abstract class TrinketUniversalTerminalMixin extends ItemWT implements Trinket {
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        inventoryTick(stack, entity.level(), entity, 0, false);
    }
}
