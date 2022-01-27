package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class NetworkingManager {

    @ExpectPlatform
    public static void registerServerBoundPacket(String name, PacketDeserializer deserializer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerClientBoundPacket(String name, PacketDeserializer deserializer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToClient(ServerPlayer player, AE2wtlibPacket packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToServer(AE2wtlibPacket packet) {
        throw new AssertionError();
    }

    @FunctionalInterface
    public interface PacketDeserializer {
        AE2wtlibPacket create(FriendlyByteBuf buf);
    }
}
