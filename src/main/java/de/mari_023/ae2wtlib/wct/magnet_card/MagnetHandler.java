package de.mari_023.ae2wtlib.wct.magnet_card;

import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import com.google.common.collect.Maps;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeableItem;

import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibTags;
import de.mari_023.ae2wtlib.networking.RestockAmountPacket;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class MagnetHandler {
    private static final WeakHashMap<ServerPlayer, Integer> players = new WeakHashMap<>();

    public static void handle(ServerPlayer player, ItemStack terminal) {
        if (players.containsKey(player) && players.get(player) == getTick(player))
            return;
        sendRestockAble(player, terminal);
        handleMagnet(player, terminal);
        players.put(player, getTick(player));
    }

    private static int getTick(ServerPlayer player) {
        if (player.getServer() == null)
            return -1;
        return player.getServer().getTickCount();
    }

    private static void sendRestockAble(ServerPlayer player, ItemStack terminal) {
        if (player.isCreative())
            return;
        if (!terminal.getOrDefault(AE2wtlibComponents.RESTOCK, false))
            return;
        sendRestockAble(player);
    }

    private static void sendRestockAble(ServerPlayer player) {
        CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack hostItem = handler.getCraftingTerminal();
        if (!hostItem.getOrDefault(AE2wtlibComponents.RESTOCK, false) || !handler.inRange())
            return;
        HashMap<Item, Long> items = new HashMap<>();

        if (handler.getTargetGrid() == null)
            return;
        KeyCounter storageList = handler.getTargetGrid().getStorageService().getCachedInventory();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty())
                continue;
            if (items.containsKey(stack.getItem()))
                continue;
            if (stack.is(AE2wtlibTags.NO_RESTOCK)) {
                continue;
            }
            AEItemKey key = AEItemKey.of(stack);
            if (key == null)
                items.put(stack.getItem(), 0L);
            else
                items.put(stack.getItem(), storageList.get(key));
        }

        HashMap<Holder<Item>, Long> map = Maps.newHashMapWithExpectedSize(items.size());
        // noinspection deprecation
        items.forEach((item, count) -> map.put(item.builtInRegistryHolder(), count));
        PacketDistributor.sendToPlayer(player, new RestockAmountPacket(map));
    }

    private static void handleMagnet(Player player, ItemStack terminal) {
        if (player.isShiftKeyDown())
            return;
        if (!getMagnetMode(terminal).magnet())
            return;
        handleMagnet(player);
    }

    private static void handleMagnet(Player player) {
        CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!getMagnetMode(ctHandler.getCraftingTerminal()).magnet())
            return;
        MagnetHost magnetHost = ctHandler.getMagnetHost();
        if (magnetHost == null)
            return;

        // if the filter is empty, it will match anything, even in whitelist mode
        if (magnetHost.getPickupFilter().isEmpty() && magnetHost.getPickupMode() == IncludeExclude.WHITELIST)
            return;

        List<ItemEntity> entityItems = player.level().getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(AE2wtlibConfig.CONFIG.magnetCardRange()),
                EntitySelector.ENTITY_STILL_ALIVE);

        for (ItemEntity entityItemNearby : entityItems) {
            var item = AEItemKey.of(entityItemNearby.getItem());
            if (item == null)
                continue;
            if (magnetHost.getPickupFilter().matchesFilter(item,
                    magnetHost.getPickupMode())
                    && !entityItemNearby.getPersistentData().contains("PreventRemoteMovement"))
                entityItemNearby.playerTouch(player);
        }
    }

    public static void saveMagnetMode(ItemStack terminal, MagnetMode magnetSettings) {
        if (magnetSettings == MagnetMode.INVALID || magnetSettings == MagnetMode.NO_CARD)
            return;
        if (!(terminal.getItem() instanceof IUpgradeableItem upgradeableItem))
            return;
        if (!upgradeableItem.getUpgrades(terminal).isInstalled(AE2wtlibItems.MAGNET_CARD))
            return;
        terminal.set(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, magnetSettings);
    }

    public static MagnetMode getMagnetMode(ItemStack terminal) {
        if (!(terminal.getItem() instanceof IUpgradeableItem upgradeableItem))
            return MagnetMode.INVALID;
        if (!upgradeableItem.getUpgrades(terminal).isInstalled(AE2wtlibItems.MAGNET_CARD))
            return MagnetMode.NO_CARD;
        return terminal.getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF);
    }
}
