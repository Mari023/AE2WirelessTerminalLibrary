package de.mari_023.ae2wtlib.networking.c2s.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.c2s.ServerNetworkManager;

public class ServerNetworkManagerImpl {
    public static void registerServerBoundPacket(String name, ServerNetworkManager.PacketDeserializer deserializer) {
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
}
