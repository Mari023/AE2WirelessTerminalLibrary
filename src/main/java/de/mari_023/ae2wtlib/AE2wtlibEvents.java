package de.mari_023.ae2wtlib;

import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.config.Actionable;
import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;

import de.mari_023.ae2wtlib.networking.UpdateRestockPacket;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

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

        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!cTHandler.inRange())
            return;
        ItemStack hostItem = cTHandler.getCraftingTerminal();
        if (!(boolean) hostItem.getOrDefault(AE2WTLibComponents.RESTOCK, false))
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
                    new PlayerSource(player, null));
        else
            changed = -cTHandler.getTargetGrid().getStorageService().getInventory().insert(
                    AEItemKey.of(item), -toAdd, Actionable.MODULATE,
                    new PlayerSource(player, null));

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
     * @param stack  The item stack being inserted
     * @param player The player attempting the insertion
     * @return True if the entire stack is successfully inserted; false otherwise.
     */
    public static boolean insertStackInME(ItemStack stack, Player player) {
        if (stack.isEmpty())
            return false;
        if (player.level().isClientSide())
            return false;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();

        if (!(MagnetHandler.getMagnetMode(terminal) == MagnetMode.PICKUP_ME))
            return false;
        if (!cTHandler.inRange())
            return false;

        MagnetHost magnetHost = cTHandler.getMagnetHost();
        if (magnetHost == null)
            return false;

        // if the filter is empty, it will match anything, even in whitelist mode
        if (magnetHost.getInsertFilter().isEmpty() && magnetHost.getInsertMode() == IncludeExclude.WHITELIST)
            return false;
        if (!magnetHost.getInsertFilter().matchesFilter(AEItemKey.of(stack), magnetHost.getInsertMode()))
            return false;

        if (cTHandler.getTargetGrid() == null)
            return false;
        if (cTHandler.getTargetGrid().getStorageService() == null)
            return false;

        long inserted = cTHandler.getTargetGrid().getStorageService().getInventory().insert(AEItemKey.of(stack),
                stack.getCount(), Actionable.MODULATE, new PlayerSource(player, null));
        int leftover = (int) (stack.getCount() - inserted);

        stack.setCount(leftover);
        return leftover == 0;
    }
}
