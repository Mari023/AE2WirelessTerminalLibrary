package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.ConfirmAutoCraftPacket;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfirmAutoCraftPacket.class, remap = false)
public class ConfirmAutoCraftPacketMixin {

    @Shadow
    @Final
    private int amount;
    @Shadow
    @Final
    private boolean autoStart;


    @Inject(method = "serverPacketData", at = @At(value = "TAIL"))
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(player.currentScreenHandler instanceof WirelessCraftAmountContainer)
            ((WirelessCraftAmountContainer) player.currentScreenHandler).confirm(amount, autoStart);
    }
}
