package de.mari_023.ae2wtlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Inject(method = "pickBlock", at = {
            @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I") })
    public void pickBlock(CallbackInfo ci, @Local ItemStack itemstack, @Local(ordinal = 1) int i) {
        if (player.getAbilities().instabuild)
            return;
        if (i != -1)
            return;
        AE2wtlibEvents.pickBlock(itemstack);
    }
}
