package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class MagnetHandler {
    public void doMagnet(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player : playerList) {
            ItemStack magnetCardHolder = getCraftingTerminal(player);
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

    public ItemStack getCraftingTerminal(PlayerEntity player) {
        PlayerInventory inv = player.inventory;
        for(int i = 0; i < inv.size(); i++) {
            ItemStack terminal = inv.getStack(i);
            if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                return terminal;
            }
        }
        return ItemStack.EMPTY;
    }
}