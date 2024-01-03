package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface AE2wtlibPacket extends CustomPacketPayload {
    /**
     * processes the packet data. the buffer has been provided by the constructor, and shouldn't be released by this
     * method
     * 
     * @param player the player that send the packet
     */
    void processPacketData(Player player);
}
