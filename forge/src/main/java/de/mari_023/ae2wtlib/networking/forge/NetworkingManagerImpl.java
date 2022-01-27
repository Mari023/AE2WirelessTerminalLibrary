package de.mari_023.ae2wtlib.networking.forge;

import net.minecraft.server.level.ServerPlayer;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.NetworkingManager;

public class NetworkingManagerImpl {
    public static void registerServerBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        // FIXME implement
    }

    public static void registerClientBoundPacket(String name, NetworkingManager.PacketDeserializer deserializer) {
        // FIXME implement
    }

    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        // FIXME implement
    }

    public static void sendToServer(AE2wtlibPacket packet) {
        // FIXME implement
    }
}
