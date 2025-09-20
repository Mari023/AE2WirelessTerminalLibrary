package de.mari_023.ae2wtlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibEvents;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "tryPickItem", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V") })
    public void pickBlock(ItemStack stack, CallbackInfo ci, @Local int i) {
        if (player.hasInfiniteMaterials())
            return;
        if (player.isSpectator())
            return;
        if (i != -1)
            return;
        AE2wtlibEvents.pickBlock(player, stack);
    }
}
