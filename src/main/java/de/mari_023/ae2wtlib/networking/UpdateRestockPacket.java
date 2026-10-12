package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;

public record UpdateRestockPacket(int slot, int count) implements AE2wtlibPacket {
    public static final Type<UpdateRestockPacket> ID = new Type<>(AE2wtlibAPI.id("update_restock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRestockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateRestockPacket::slot,
            ByteBufCodecs.INT, UpdateRestockPacket::count,
            UpdateRestockPacket::new);

    @Override
    public void processPacketData(Player player) {
        player.getInventory().getItem(slot()).setCount(count());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
