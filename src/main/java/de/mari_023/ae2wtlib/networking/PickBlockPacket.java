package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibEvents;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;

public record PickBlockPacket(ItemStack itemStack) implements AE2wtlibPacket {
    public static final Type<PickBlockPacket> ID = new Type<>(AE2wtlibAPI.id("pick_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PickBlockPacket> STREAM_CODEC = ItemStack.STREAM_CODEC
            .map(PickBlockPacket::new, PickBlockPacket::itemStack);

    @Override
    public void processPacketData(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;
        AE2wtlibEvents.pickBlock(serverPlayer, itemStack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
