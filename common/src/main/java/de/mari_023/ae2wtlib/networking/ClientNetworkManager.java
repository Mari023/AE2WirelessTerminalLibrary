package de.mari_023.ae2wtlib.networking;

import de.mari_023.ae2wtlib.networking.c2s.ServerNetworkManager;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ClientNetworkManager {

    @ExpectPlatform
    public static void registerClientBoundPacket(String name, ServerNetworkManager.PacketDeserializer deserializer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToServer(AE2wtlibPacket packet) {
        throw new AssertionError();
    }
}
