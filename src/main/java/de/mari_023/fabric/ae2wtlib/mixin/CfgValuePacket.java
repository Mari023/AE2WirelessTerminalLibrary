package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.container.implementations.WirelessCraftConfirmContainer;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.ConfigValuePacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfigValuePacket.class, remap = false)
public class CfgValuePacket {

    @Shadow
    @Final
    private String Name;

    @Inject(method = "serverPacketData", at = @At(value = "TAIL"))
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(Name.equals("Terminal.Start") && player.currentScreenHandler instanceof WirelessCraftConfirmContainer)
            ((WirelessCraftConfirmContainer) player.currentScreenHandler).startJob();
    }
}