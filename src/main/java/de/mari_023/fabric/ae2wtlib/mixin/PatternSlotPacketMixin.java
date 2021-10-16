package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PatternSlotPacket;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternSlotPacket.class, remap = false)
public class PatternSlotPacketMixin {

    @Inject(method = "serverPacketData", at = @At(value = "HEAD"))
    public void clientPacketData(INetworkInfo network, ServerPlayerEntity player, CallbackInfo ci) {
        if(player.currentScreenHandler instanceof WPTContainer patternTerminal) {
            patternTerminal.craftOrGetItem((PatternSlotPacket) (Object) this);
        }
    }
}
