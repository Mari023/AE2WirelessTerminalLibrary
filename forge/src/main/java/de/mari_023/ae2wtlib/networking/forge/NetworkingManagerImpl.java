package de.mari_023.ae2wtlib.networking.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.NetworkingManager;
import dev.architectury.networking.NetworkManager;

public class NetworkingManagerImpl {
    public static void registerServerBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (buf, context) -> {
                    buf.retain();
                    context.queue(() -> {
                        deserializer.create(buf).processPacketData(context.getPlayer());
                        buf.release();
                    });
                });
    }

    public static void registerClientBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        NetworkManager.registerReceiver(NetworkManager.s2c(), new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (buf, context) -> {
                    buf.retain();
                    context.queue(() -> {
                        if (context.getPlayer() == null)
                            return;
                        buf.retain();
                        deserializer.create(buf).processPacketData(context.getPlayer());
                        buf.release();
                    });
                });
    }

    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        NetworkManager.sendToPlayer(player, new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }

    public static void sendToServer(AE2wtlibPacket packet) {
        NetworkManager.sendToServer(new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }
}
