package de.mari_023.ae2wtlib.networking.forge;

import net.minecraft.resources.ResourceLocation;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.c2s.ServerNetworkManager;
import dev.architectury.networking.NetworkManager;

public class ClientNetworkManagerImpl {

    public static void registerClientBoundPacket(String name, ServerNetworkManager.PacketDeserializer deserializer) {
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

    public static void sendToServer(AE2wtlibPacket packet) {
        NetworkManager.sendToServer(new ResourceLocation(AE2wtlib.MOD_NAME, packet.getPacketName()),
                packet.getPacketBuffer());
    }
}
