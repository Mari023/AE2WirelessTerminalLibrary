package de.mari_023.ae2wtlib.networking.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.NetworkingManager;

public class NetworkingManagerImpl {
    public static void registerServerBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (server, player, handler, buf, sender) -> {
                    buf.retain();
                    server.execute(() -> {
                        deserializer.create(buf).processPacketData(player);
                        buf.release();
                    });
                });
    }

    public static void registerClientBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (client, handler, buf, responseSender) -> {
                    if (client.player == null)
                        return;
                    buf.retain();
                    client.execute(() -> {
                        deserializer.create(buf).processPacketData(client.player);
                        buf.release();
                    });
                });
    }

    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        ServerPlayNetworking.send(player, new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }

    public static void sendToServer(AE2wtlibPacket packet) {
        ClientPlayNetworking.send(new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }
}
