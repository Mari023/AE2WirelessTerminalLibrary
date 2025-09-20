package de.mari_023.ae2wtlib;

import java.util.function.Consumer;

import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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

        int toAdd = item.getMaxStackSize() - count;
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

        int slot = player.getInventory().findSlotMatchingItem(item);//TODO 1.21.8 test if that still works properly
        if (slot == -1) {
            if (ItemStack.isSameItemSameComponents(player.getInventory().getItem(Inventory.SLOT_OFFHAND), item))
                slot = Inventory.SLOT_OFFHAND;
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

        if (!canInsert(stack, player))
            return;

        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!cTHandler.inRange())
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

    private static boolean canInsert(ItemStack stack, Player player) {
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();

        cTHandler.checkTerminal();
        if (cTHandler.isRestockEnabled() && isRestocking(stack, player))
            return true;

        if (player.isShiftKeyDown())
            return false;

        if (!(MagnetHandler.getMagnetMode(terminal).pickupToME()))
            return false;

        MagnetHost magnetHost = cTHandler.getMagnetHost();
        if (magnetHost == null)
            return false;

        // if the filter is empty, it will match anything, even in whitelist mode
        if (magnetHost.getInsertFilter().isEmpty() && magnetHost.getInsertMode() == IncludeExclude.WHITELIST)
            return false;
        if (!magnetHost.getInsertFilter().matchesFilter(AEItemKey.of(stack), magnetHost.getInsertMode()))
            return false;

        return true;
    }

    private static boolean isRestocking(ItemStack stack, Player player) {
        if (stack.is(AE2wtlibTags.NO_RESTOCK)) {
            return false;
        }
        if (stack.getMaxStackSize() == 1) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            if (ItemStack.isSameItemSameComponents(stack, player.getInventory().getItem(i)))
                return true;
        }
        return false;
    }

    public static void pickBlock(ItemStack stack) {
        ClientPacketDistributor.sendToServer(new PickBlockPacket(stack));
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
        inventory.setSelectedSlot(targetSlot);
        player.connection.send(new ClientboundSetCarriedItemPacket(player.getInventory().getSelectedSlot()));
    }
}
