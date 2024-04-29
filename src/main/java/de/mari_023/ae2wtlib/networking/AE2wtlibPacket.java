package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface AE2wtlibPacket extends CustomPacketPayload {
    /**
     * processes the packet data.
     * 
     * @param player the player that send the packet
     */
    void processPacketData(Player player);
}
