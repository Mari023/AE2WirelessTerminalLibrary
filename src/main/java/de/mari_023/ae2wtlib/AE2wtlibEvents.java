package de.mari_023.ae2wtlib;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;
import de.mari_023.ae2wtlib.networking.ServerNetworkManager;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntConsumer;

public class AE2wtlibEvents {
    public static boolean restock(ServerPlayer player, ItemStack item, int count, IntConsumer itemCounter) {
        if (player.isCreative())
            return true;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!cTHandler.inRange() || !ItemWT.getBoolean(cTHandler.getCraftingTerminal(), "restock")
                || cTHandler.getTargetGrid() == null || cTHandler.getTargetGrid().getStorageService() == null)
            return true;
        int toAdd = Math.max(item.getMaxStackSize() / 2, 1) - count;
        if (toAdd == 0)
            return true;

        long changed;
        if (toAdd > 0)
            changed = cTHandler.getTargetGrid().getStorageService().getInventory().extract(
                    AEItemKey.of(item), toAdd, Actionable.MODULATE,
                    new PlayerSource(player, null));
        else
            changed = -cTHandler.getTargetGrid().getStorageService().getInventory().insert(
                    AEItemKey.of(item), -toAdd, Actionable.MODULATE,
                    new PlayerSource(player, null));

        itemCounter.accept(count + (int) changed);
        return false;
    }

    public static boolean insertStackInME(ItemStack stack, Player player) {
        if (stack.isEmpty())
            return false;
        CraftingTerminalHandler cTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = cTHandler.getCraftingTerminal();

        if (!(MagnetHandler.getMagnetSettings(terminal).magnetMode == MagnetMode.PICKUP_ME))
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
        ServerNetworkManager.sendToClient((ServerPlayer) player, new UpdateRestockPacket(
                player.getInventory().findSlotMatchingUnusedItem(stack), stack.getCount()));
        return leftover == 0;
    }
}
