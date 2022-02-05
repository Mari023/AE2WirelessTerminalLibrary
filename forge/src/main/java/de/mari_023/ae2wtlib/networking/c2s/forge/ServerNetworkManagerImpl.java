package de.mari_023.ae2wtlib.networking.c2s.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.c2s.ServerNetworkManager;
import dev.architectury.networking.NetworkManager;

public class ServerNetworkManagerImpl {
    public static void registerServerBoundPacket(String name, ServerNetworkManager.PacketDeserializer deserializer) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), new ResourceLocation(AE2wtlib.MOD_NAME, name),
                (buf, context) -> {
                    buf.retain();
                    context.queue(() -> {
                        deserializer.create(buf).processPacketData(context.getPlayer());
                        buf.release();
                    });
                });
    }

    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        NetworkManager.sendToPlayer(player, new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }
}
