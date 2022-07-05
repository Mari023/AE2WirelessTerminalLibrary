package de.mari_023.ae2wtlib.networking.s2c;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class RestockAmountPacket extends AE2wtlibPacket {

    public static final String NAME = "restock_amounts";

    public RestockAmountPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public RestockAmountPacket(HashMap<Item, Long> items) {
        super(createBuffer());
        for (Map.Entry<Item, Long> entry : items.entrySet()) {
            buf.writeItem(new ItemStack(entry.getKey()));
            buf.writeLong(entry.getValue());
        }
    }

    @Override
    public void processPacketData(Player player) {
        CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        HashMap<Item, Long> items = new HashMap<>();
        while (buf.isReadable())
            items.put(buf.readItem().getItem(), buf.readLong());
        ctHandler.setRestockAbleItems(items);
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
