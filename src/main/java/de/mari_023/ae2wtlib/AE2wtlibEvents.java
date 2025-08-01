package de.mari_023.ae2wtlib;

import java.util.function.Consumer;

import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.config.Actionable;
import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibTags;
import de.mari_023.ae2wtlib.networking.PickBlockPacket;
import de.mari_023.ae2wtlib.networking.UpdateRestockPacket;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;

public class AE2wtlibEvents {
    /**
     * Attempts to restock an item in a player's inventory from the ME system.
     *
     * @param player   The ServerPlayer
     * @param item     The ItemStack that should be restocked
     * @param count    Current count of the item
     * @param setStack A callback for setting the final modified item stack
     */
    public static void restock(ServerPlayer player, ItemStack item, int count, Consumer<ItemStack> setStack) {
        if (player.isCreative())
            return;
        if (item.isEmpty())
            return;
        if (item.is(AE2wtlibTags.NO_RESTOCK)) {
            return;
        }

        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!cTHandler.inRange())
            return;
        ItemStack hostItem = cTHandler.getCraftingTerminal();
        if (!(boolean) hostItem.getOrDefault(AE2wtlibComponents.RESTOCK, false))
            return;
        if (cTHandler.getTargetGrid() == null)
            return;
        if (cTHandler.getTargetGrid().getStorageService() == null)
            return;

        int toAdd = Math.max(item.getMaxStackSize() / 2, 1) - count;
        if (toAdd == 0)
            return;

        long changed;
        if (toAdd > 0)
            changed = cTHandler.getTargetGrid().getStorageService().getInventory().extract(
                    AEItemKey.of(item), toAdd, Actionable.MODULATE,
                    new PlayerSource(player));
        else
            changed = -cTHandler.getTargetGrid().getStorageService().getInventory().insert(
                    AEItemKey.of(item), -toAdd, Actionable.MODULATE,
                    new PlayerSource(player));

        item.setCount(count + (int) changed);
        setStack.accept(item);

        int slot = player.getInventory().findSlotMatchingUnusedItem(item);
        if (slot == -1) {
            if (player.getInventory().offhand.contains(item))
                slot = Inventory.INVENTORY_SIZE;
        }
        PacketDistributor.sendToPlayer(player, new UpdateRestockPacket(slot, item.getCount()));
    }

    /**
     * Attempts to insert an item stack from a player's inventory into an ME system linked to a terminal the player
     * carries.
     *
     * @param entity The item stack entity being inserted
     * @param player The player attempting the insertion
     */
    public static void insertStackInME(ItemEntity entity, Player player) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty())
            return;
        if (player.level().isClientSide())
            return;
        if (player.isShiftKeyDown())
            return;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();

        if (!(MagnetHandler.getMagnetMode(terminal).pickupToME()))
            return;
        if (!cTHandler.inRange())
            return;

        MagnetHost magnetHost = cTHandler.getMagnetHost();
        if (magnetHost == null)
            return;

        // if the filter is empty, it will match anything, even in whitelist mode
        if (magnetHost.getInsertFilter().isEmpty() && magnetHost.getInsertMode() == IncludeExclude.WHITELIST)
            return;
        if (!magnetHost.getInsertFilter().matchesFilter(AEItemKey.of(stack), magnetHost.getInsertMode()))
            return;

        if (cTHandler.getTargetGrid() == null)
            return;
        if (cTHandler.getTargetGrid().getStorageService() == null)
            return;

        long inserted = cTHandler.getTargetGrid().getStorageService().getInventory().insert(AEItemKey.of(stack),
                stack.getCount(), Actionable.MODULATE, new PlayerSource(player));
        int leftover = (int) (stack.getCount() - inserted);

        player.awardStat(Stats.ITEM_PICKED_UP.get(stack.getItem()), (int) inserted);
        player.onItemPickup(entity);

        stack.setCount(leftover);
    }

    public static void pickBlock(ItemStack stack) {
        PacketDistributor.sendToServer(new PickBlockPacket(stack));
    }

    public static void pickBlock(ServerPlayer player, ItemStack stack) {
        var cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();
        if (!terminal.getOrDefault(AE2wtlibComponents.PICK_BLOCK, false))
            return;

        if (cTHandler.getTargetGrid() == null)
            return;
        if (cTHandler.getTargetGrid().getStorageService() == null)
            return;
        var networkInventory = cTHandler.getTargetGrid().getStorageService().getInventory();
        var playerSource = new PlayerSource(player);

        var inventory = player.getInventory();
        int targetSlot = inventory.getSuitableHotbarSlot();
        var toReplace = inventory.getItem(targetSlot);

        var insert = networkInventory.insert(AEItemKey.of(toReplace), toReplace.getCount(), Actionable.SIMULATE,
                playerSource);
        if (insert < toReplace.getCount())
            return;
        int targetAmount = stack.getMaxStackSize();
        if (!stack.is(AE2wtlibTags.NO_RESTOCK) && targetAmount != 1) {
            targetAmount /= 2;
        }
        var extracted = networkInventory.extract(AEItemKey.of(stack), targetAmount, Actionable.SIMULATE, playerSource);
        if (extracted == 0)
            return;

        insert = networkInventory.insert(AEItemKey.of(toReplace), toReplace.getCount(), Actionable.MODULATE,
                playerSource);
        if (insert < toReplace.getCount()) {
            toReplace.setCount(toReplace.getCount() - (int) insert);
            inventory.setItem(targetSlot, toReplace);
            return;
        }

        extracted = networkInventory.extract(AEItemKey.of(stack), targetAmount, Actionable.MODULATE, playerSource);
        if (extracted == 0) {
            inventory.setItem(targetSlot, ItemStack.EMPTY);
            return;
        }
        stack.setCount((int) extracted);
        inventory.setItem(targetSlot, stack);
        inventory.selected = targetSlot;
        player.connection.send(new ClientboundSetCarriedItemPacket(player.getInventory().selected));
    }
}
