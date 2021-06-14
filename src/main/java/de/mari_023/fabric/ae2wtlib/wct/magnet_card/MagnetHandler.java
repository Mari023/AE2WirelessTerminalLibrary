package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.Api;
import appeng.me.cache.NetworkMonitor;
import appeng.util.item.AEItemStack;
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
                List<ItemEntity> entityItems = player.getServerWorld().getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(16.0D), EntityPredicates.VALID_ENTITY);
                for(ItemEntity entityItemNearby : entityItems)
                    if(!player.isSneaking()) entityItemNearby.onPlayerCollision(player);
            }
            sendRestockAble(player);
        }
    }


    private boolean sendEmpty;

    public void sendRestockAble(ServerPlayerEntity player) {
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        boolean canRestock = !player.isCreative() && ItemWT.getBoolean(handler.getCraftingTerminal(), "restock") && handler.inRange();
        HashMap<Item, Long> items = new HashMap<>();

        IItemList<IAEItemStack> storageList = ((NetworkMonitor<IAEItemStack>) ((IStorageGrid) handler.getTargetGrid().getCache(IStorageGrid.class)).getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class))).getStorageList();

        if(canRestock) for(int i = 0; i < player.inventory.size(); i++) {
            ItemStack stack = player.inventory.getStack(i);
            if(stack.isEmpty()) continue;
            if(!items.containsKey(stack.getItem())) items.put(stack.getItem(), getCount(storageList, stack));
        }

        PacketByteBuf buf = PacketByteBufs.create();
        for(Map.Entry<Item, Long> entry : items.entrySet())
            AEItemStack.fromItemStack(new ItemStack(entry.getKey())).setStackSize(entry.getValue()).writeToPacket(buf);
        if(canRestock || sendEmpty)
            ServerPlayNetworking.send(player, new Identifier("ae2wtlib", "restock_amounts"), buf);
        if(canRestock) sendEmpty = true;
        else if(sendEmpty) sendEmpty = false;
    }

    private long getCount(IItemList<IAEItemStack> storageList, ItemStack stack) {
        IAEItemStack aeStack = storageList.findPrecise(AEItemStack.fromItemStack(stack));
        return aeStack == null ? 0 : aeStack.getStackSize();
    }
}