package de.mari_023.ae2wtlib.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;

public record UpdateRestockPacket(int slot, ItemStack itemStack) implements AE2wtlibPacket {
    public static final Type<UpdateRestockPacket> ID = new Type<>(AE2wtlibAPI.id("update_restock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRestockPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateRestockPacket::slot,
            ItemStack.OPTIONAL_STREAM_CODEC, UpdateRestockPacket::itemStack,
            UpdateRestockPacket::new);

    @Override
    public void processPacketData(Player player) {
        switch (slot()) {
            case Inventory.INVENTORY_SIZE -> player.getInventory().offhand.set(0, itemStack());
            case -1 -> {
            }
            default -> player.getInventory().setItem(slot(), itemStack());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
