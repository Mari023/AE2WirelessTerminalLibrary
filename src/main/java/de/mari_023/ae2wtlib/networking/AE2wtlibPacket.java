package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import io.netty.buffer.Unpooled;

public abstract class AE2wtlibPacket {
    protected final FriendlyByteBuf buf;

    public AE2wtlibPacket(FriendlyByteBuf buf) {
        this.buf = buf;
    }

    public static FriendlyByteBuf createBuffer() {
        return new FriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()));
    }

    /**
     * processes the packet data. the buffer has been provided by the constructor, and shouldn't be released by this
     * method
     * 
     * @param player the player that send the packet
     */
    public abstract void processPacketData(Player player);

    public FriendlyByteBuf getPacketBuffer() {
        return buf;
    }

    public abstract String getPacketName();
}
