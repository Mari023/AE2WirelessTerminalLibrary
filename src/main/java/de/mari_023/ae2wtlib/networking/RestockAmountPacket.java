package de.mari_023.ae2wtlib.networking;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public record RestockAmountPacket(HashMap<Item, Long> items) implements AE2wtlibPacket {
    public static final ResourceLocation ID = AE2wtlib.makeID("restock_amounts");

    public RestockAmountPacket(FriendlyByteBuf buf) {
        this(readMap(buf));
    }

    private static HashMap<Item, Long> readMap(FriendlyByteBuf buf) {
        HashMap<Item, Long> items = new HashMap<>();
        while (buf.isReadable())
            items.put(buf.readItem().getItem(), buf.readLong());
        return items;
    }

    @Override
    public void processPacketData(Player player) {
        CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ctHandler.setRestockAbleItems(items);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        for (Map.Entry<Item, Long> entry : items.entrySet()) {
            buf.writeItem(new ItemStack(entry.getKey()));
            buf.writeLong(entry.getValue());
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
