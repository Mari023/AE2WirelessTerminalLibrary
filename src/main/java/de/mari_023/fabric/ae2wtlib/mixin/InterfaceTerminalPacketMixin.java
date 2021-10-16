package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InterfaceTerminalPacket;
import de.mari_023.fabric.ae2wtlib.wit.WITScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = InterfaceTerminalPacket.class, remap = false)
public class InterfaceTerminalPacketMixin {
    @Final
    @Shadow
    private boolean fullUpdate;
    @Final
    @Shadow
    private NbtCompound in;

    @Inject(method = "clientPacketData", at = @At(value = "HEAD"))
    public void clientPacketData(INetworkInfo network, PlayerEntity player, CallbackInfo ci) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen instanceof WITScreen witScreen) {
            witScreen.postUpdate(fullUpdate, in);
        }
    }
}
