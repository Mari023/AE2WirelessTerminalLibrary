package de.mari_023.fabric.ae2wtlib.mixin;

import com.google.gson.JsonObject;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
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
        if(value.get("type").getAsString().equals("ae2wtlib.open")) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(value.get("data").getAsString());
            ClientPlayNetworking.send(new Identifier(ae2wtlib.MOD_NAME, "hotkey"), buf);
            MinecraftClient.getInstance().setScreen(null);
        }
    }
}