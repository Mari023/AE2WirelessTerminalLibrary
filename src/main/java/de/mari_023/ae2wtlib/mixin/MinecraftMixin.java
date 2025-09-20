package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Inject(method = "pickBlock", at = {
            @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I") }, locals = LocalCapture.CAPTURE_FAILHARD)
    public void pickBlock(CallbackInfo ci,
            boolean flag,
            BlockEntity blockentity,
            HitResult.Type hitresult$type,
            ItemStack itemstack,
            Inventory inventory,
            int i) {
        if (player.getAbilities().instabuild)
            return;
        if (player.isSpectator())
            return;
        if (i != -1)
            return;
        AE2wtlibEvents.pickBlock(itemstack);
    }
}
