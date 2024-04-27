package de.mari_023.ae2wtlib.networking;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public record RestockAmountPacket(HashMap<Holder<Item>, Long> items) implements AE2wtlibPacket {
    public static final Type<RestockAmountPacket> ID = new Type<>(AE2wtlib.id("restock_amounts"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RestockAmountPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    Maps::newHashMapWithExpectedSize,
                    ByteBufCodecs.holderRegistry(Registries.ITEM),
                    ByteBufCodecs.VAR_LONG),
            RestockAmountPacket::items,
            RestockAmountPacket::new);

    @Override
    public void processPacketData(Player player) {
        CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        HashMap<Item, Long> map = Maps.newHashMapWithExpectedSize(items().size());
        items().forEach((item, count) -> map.put(item.value(), count));
        ctHandler.setRestockAbleItems(map);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
