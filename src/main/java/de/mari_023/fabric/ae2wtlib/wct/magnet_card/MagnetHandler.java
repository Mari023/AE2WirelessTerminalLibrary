package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import appeng.api.storage.data.AEItemKey;
import appeng.api.storage.data.KeyCounter;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagnetHandler {
    public void doMagnet(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player : playerList) {
            if(ItemMagnetCard.isActiveMagnet(CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal())) {
                List<ItemEntity> entityItems = player.getWorld().getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(ae2wtlibConfig.INSTANCE.magnetCardRange()), EntityPredicates.VALID_ENTITY);
                boolean sneaking = !player.isSneaking();
                for(ItemEntity entityItemNearby : entityItems) if(sneaking) entityItemNearby.onPlayerCollision(player);
            }
            sendRestockAble(player);
        }
    }

    public void sendRestockAble(ServerPlayerEntity player) {
        try {
            CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
            if(player.isCreative() || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") || !handler.inRange())
                return;
            HashMap<Item, Long> items = new HashMap<>();

            if(handler.getItemStorageChannel() == null) return;
            KeyCounter storageList = handler.getItemStorageChannel().getCachedAvailableStacks();

            for(int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if(stack.isEmpty()) continue;
                if(!items.containsKey(stack.getItem())) items.put(stack.getItem(), getCount(storageList, stack));
            }

            PacketByteBuf buf = PacketByteBufs.create();
            for(Map.Entry<Item, Long> entry : items.entrySet()) {
                buf.writeItemStack(new ItemStack(entry.getKey()));
                buf.writeLong(entry.getValue());
            }
            ServerPlayNetworking.send(player, new Identifier(ae2wtlib.MOD_NAME, "restock_amounts"), buf);
        } catch(NullPointerException ignored) {}
    }

    private long getCount(KeyCounter storageList, ItemStack stack) {
        return storageList.get(AEItemKey.of(stack));
    }
}