package de.mari_023.fabric.ae2wtlib.mixin;

import com.google.gson.JsonObject;
import me.ultrablacklinux.minemenufabric.client.screen.MineMenuSelectScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = MineMenuSelectScreen.class, remap = false)
public class MineMenuTerminalHandler {

    @Inject(method = "handleTypes(Lcom/google/gson/JsonObject;)V", at = @At(value = "INVOKE"))
    void handleTypes(JsonObject value, CallbackInfo ci) {
        String type = value.get("type").getAsString();
        if(!type.equals("ae2wtlib.open")) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(value.get("data").getAsString());
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
        MinecraftClient.getInstance().openScreen(null);
    }
}