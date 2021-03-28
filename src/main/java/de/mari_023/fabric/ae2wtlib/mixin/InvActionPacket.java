package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InventoryActionPacket;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryActionPacket.class)
public class InvActionPacket {

    @Inject(method = "serverPacketData", at = @At(value = "TAIL"), require = 1, allow = 1)
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        System.out.println("mixed in!");
    }
}