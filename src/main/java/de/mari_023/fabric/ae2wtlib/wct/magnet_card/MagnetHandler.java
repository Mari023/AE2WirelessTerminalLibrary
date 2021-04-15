package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class MagnetHandler {
    public void doMagnet(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player : playerList) {
            ItemStack magnetCardHolder = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
            if(ItemMagnetCard.isActiveMagnet(magnetCardHolder)) {
                List<ItemEntity> entityItems = player.getServerWorld().getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(16.0D), EntityPredicates.VALID_ENTITY);
                for(ItemEntity entityItemNearby : entityItems) {
                    if(!player.isSneaking()) {
                        entityItemNearby.onPlayerCollision(player);
                    }
                }
            }
        }
    }
}