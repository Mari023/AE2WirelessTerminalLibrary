package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.MEInventoryUpdatePacket;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftConfirmScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MEInventoryUpdatePacket.class, remap = false)
public class MEInvUpdatePacket {

    /*@Shadow
    @Final
    private List<IAEItemStack> list;
    @Shadow
    @Final
    private byte ref;

    @Environment(EnvType.CLIENT)
    @Inject(method = "clientPacketData", at = @At(value = "TAIL"))
    public void clientPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(MinecraftClient.getInstance().currentScreen instanceof WirelessCraftConfirmScreen)
            ((WirelessCraftConfirmScreen) MinecraftClient.getInstance().currentScreen).postUpdate(list, ref);
    }*/
}