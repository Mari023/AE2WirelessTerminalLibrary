package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
                List<ItemEntity> entityItems = player.getServerWorld().getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(16.0D), EntityPredicates.VALID_ENTITY);
                for(ItemEntity entityItemNearby : entityItems) {
                    if(!player.isSneaking()) {
                        entityItemNearby.onPlayerCollision(player);
                    }
                }
            }
            sendRestockAble(player);
        }
    }


    private boolean sendEmpty;
    public void sendRestockAble(ServerPlayerEntity player) {//FIXME wait until the player properly joined
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        boolean canRestock = !player.isCreative() && ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") && handler.inRange();
        HashMap<Item, Integer> items = new HashMap<>();
        if(canRestock) {
            for(int i = 0; i < player.inventory.size(); i++) {
                ItemStack stack = player.inventory.getStack(i);
                if(!items.containsKey(stack.getItem())) {
                    items.put(stack.getItem(), getCount(player, stack));
                }
            }
        }

        PacketByteBuf buf = PacketByteBufs.create();
        for(Map.Entry<Item, Integer> entry : items.entrySet())
            buf.writeItemStack(new ItemStack(entry.getKey(), entry.getValue()));
        if(canRestock || sendEmpty) ClientPlayNetworking.send(new Identifier("ae2wtlib", "restock_amounts"), buf);
        if(canRestock) sendEmpty = true;
        else if(sendEmpty) sendEmpty = false;
    }

    private int getCount(PlayerEntity player, ItemStack stack) {
        return 0;//TODO actually get the amount
    }
}