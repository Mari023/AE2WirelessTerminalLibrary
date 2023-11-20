package de.mari_023.ae2wtlib.networking;

import de.mari_023.ae2wtlib.AE2wtlib;
import dev.architectury.networking.NetworkManager;

public class ClientNetworkManager {

    public static void registerClientBoundPacket(String name, ServerNetworkManager.PacketDeserializer deserializer) {
        NetworkManager.registerReceiver(NetworkManager.s2c(), AE2wtlib.makeID(name),
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

    public static void sendToServer(AE2wtlibPacket packet) {
        NetworkManager.sendToServer(AE2wtlib.makeID(packet.getPacketName()),
                packet.getPacketBuffer());
    }
}
