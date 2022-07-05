package de.mari_023.ae2wtlib.networking;

import de.mari_023.ae2wtlib.AE2wtlib;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;

public class ServerNetworkManager {

    public static void registerServerBoundPacket(String name, PacketDeserializer deserializer) {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (server, player, handler, buf, sender) -> {
                    buf.retain();
                    server.execute(() -> {
                        deserializer.create(buf).processPacketData(player);
                        buf.release();
                    });
                });
    }

    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        ServerPlayNetworking.send(player, new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }

    @FunctionalInterface
    public interface PacketDeserializer {
        AE2wtlibPacket create(FriendlyByteBuf buf);
    }
}
