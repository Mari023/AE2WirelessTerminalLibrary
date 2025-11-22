package de.mari_023.ae2wtlib;

import java.util.function.Consumer;

import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.ServerNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.PickBlockPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;
import appeng.menu.me.crafting.CraftAmountMenu;

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
        if (!cTHandler.inRange() || !ItemWT.getBoolean(cTHandler.getCraftingTerminal(), AE2wtlibTags.RESTOCK)
                || cTHandler.getTargetGrid() == null || cTHandler.getTargetGrid().getStorageService() == null)
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
        ServerNetworkManager.sendToClient(player, new UpdateRestockPacket(
                player.getInventory().findSlotMatchingUnusedItem(item), item.getCount()));
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
        if (stack.isEmpty() || player.level().isClientSide())
            return false;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();

        if (!(MagnetHandler.getMagnetMode(terminal).pickupToME()))
            return false;
        if (!cTHandler.inRange())
            return false;

        MagnetHost magnetHost = cTHandler.getMagnetHost();
        if (magnetHost == null)
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

    public static void pickBlock(ItemStack stack) {
        ClientNetworkManager.sendToServer(new PickBlockPacket(stack));
    }

    public static void pickBlock(ServerPlayer player, ItemStack stack) {
        var cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();
        if (!ItemWT.getBoolean(terminal, AE2wtlibTags.PICK_BLOCK))
            return;

        if (cTHandler.getTargetGrid() == null)
            return;
        if (cTHandler.getTargetGrid().getStorageService() == null)
            return;
        var networkInventory = cTHandler.getTargetGrid().getStorageService().getInventory();
        var playerSource = new PlayerSource(player, null);

        var inventory = player.getInventory();
        int targetSlot = inventory.getSuitableHotbarSlot();
        var toReplace = inventory.getItem(targetSlot);

        var insert = networkInventory.insert(AEItemKey.of(toReplace), toReplace.getCount(), Actionable.SIMULATE,
                playerSource);
        if (insert < toReplace.getCount())
            return;
        int targetAmount = Math.max(1, stack.getMaxStackSize() / 2);
        var what = AEItemKey.of(stack);
        var extracted = networkInventory.extract(what, targetAmount, Actionable.SIMULATE, playerSource);
        if (extracted == 0) {
            if (!ItemWT.getBoolean(terminal, AE2wtlibTags.CRAFT_IF_MISSING)
                    || cTHandler.getTargetGrid().getCraftingService().getCraftingFor(what).isEmpty())
                return;
            CraftAmountMenu.open(player, cTHandler.getLocator(), what, 1);
            return;
        }

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
