package de.mari_023.ae2wtlib.wct.magnet_card;

import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.Platform;
import de.mari_023.ae2wtlib.networking.ServerNetworkManager;
import de.mari_023.ae2wtlib.networking.s2c.RestockAmountPacket;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeableItem;

public class MagnetHandler {
    public void doMagnet(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            doMagnet(player);
        }
    }

    public static void doMagnet(ServerPlayer player) {
        handleMagnet(player);
        sendRestockAble(player);
    }

    private static void sendRestockAble(ServerPlayer player) {
        try {
            CraftingTerminalHandler handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
            if (player.isCreative() || !ItemWT.getBoolean(handler.getCraftingTerminal(), "restock")
                    || !handler.inRange())
                return;
            HashMap<Item, Long> items = new HashMap<>();

            if (handler.getTargetGrid() == null || handler.getTargetGrid().getStorageService().getInventory() == null)
                return;
            KeyCounter storageList = handler.getTargetGrid().getStorageService().getInventory().getAvailableStacks();

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty())
                    continue;
                if (items.containsKey(stack.getItem()))
                    continue;
                AEItemKey key = AEItemKey.of(stack);
                if (key == null)
                    items.put(stack.getItem(), 0L);
                else
                    items.put(stack.getItem(), storageList.get(key));
            }

            ServerNetworkManager.sendToClient(player, new RestockAmountPacket(items));
        } catch (NullPointerException ignored) {// TODO is this even necessary anymore?
        }
    }

    private static void handleMagnet(Player player) {
        CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!getMagnetSettings(ctHandler.getCraftingTerminal()).isActive())
            return;
        MagnetHost magnetHost = ctHandler.getMagnetHost();
        if (magnetHost == null)
            return;

        List<ItemEntity> entityItems = player.getLevel().getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(AE2wtlibConfig.INSTANCE.magnetCardRange()),
                EntitySelector.ENTITY_STILL_ALIVE);
        if (player.isShiftKeyDown())
            return;
        for (ItemEntity entityItemNearby : entityItems) {
            if (magnetHost.getPickupFilter().matchesFilter(AEItemKey.of(entityItemNearby.getItem()),
                    magnetHost.getPickupMode())
                    && !Platform.preventRemoteMovement(entityItemNearby))
                entityItemNearby.playerTouch(player);
        }
    }

    public static void saveMagnetSettings(ItemStack terminal, MagnetSettings magnetSettings) {
        if (terminal.getItem() instanceof IUpgradeableItem upgradeableItem
                && upgradeableItem.getUpgrades(terminal).isInstalled(AE2wtlib.MAGNET_CARD))
            terminal.getOrCreateTag().put("magnet_settings", magnetSettings.toTag());
    }

    public static MagnetSettings getMagnetSettings(ItemStack terminal) {
        if (terminal.getItem() instanceof IUpgradeableItem upgradeableItem
                && upgradeableItem.getUpgrades(terminal).isInstalled(AE2wtlib.MAGNET_CARD))
            return new MagnetSettings((CompoundTag) terminal.getOrCreateTag().get("magnet_settings"));
        return new MagnetSettings();
    }
}
