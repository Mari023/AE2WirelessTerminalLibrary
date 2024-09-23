package de.mari_023.ae2wtlib.api.terminal;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.integration.modules.curios.CuriosIntegration;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

/**
 * This class handles functionalities related to the Wireless Universal Terminal (WUT). It provides methods to get and
 * set the current terminal, check for a specific terminal, cycle through terminals and update client terminals
 */
public class WUTHandler {
    /**
     * Set the current terminal of a given item stack.
     *
     * @param player    The player.
     * @param locator   The menu locator.
     * @param itemStack The ItemStack to update.
     * @param terminal  The terminal name to set.
     */
    public static void setCurrentTerminal(Player player, ItemMenuHostLocator locator, ItemStack itemStack,
            WTDefinition terminal) {
        if (!(itemStack.getItem() instanceof ItemWUT))
            return;
        if (!hasTerminal(itemStack, terminal))
            return;
        itemStack.set(AE2wtlibComponents.CURRENT_TERMINAL, terminal);
        updateClientTerminal((ServerPlayer) player, locator, itemStack);
    }

    /**
     * Checks if the given terminal item stack contains the specified terminal name.
     *
     * @param stack    The terminal ItemStack.
     * @param terminal The terminal name to check.
     * @return true if the terminal contains the specified terminal name, false otherwise.
     */
    @Contract(pure = true)
    public static boolean hasTerminal(ItemStack stack, WTDefinition terminal) {
        if (stack.isEmpty())
            return false;
        if (stack.getItem() instanceof ItemWUT) {
            return stack.get(terminal.componentType()) != null;
        }
        return stack.getItem().equals(terminal.item());
    }

    /**
     * Cycles to the next terminal in the item stack based on user input.
     *
     * @param player               The player.
     * @param locator              The menu locator.
     * @param itemStack            The terminal ItemStack to update.
     * @param isHandlingRightClick If true, cycles to the previous terminal; otherwise, cycles to the next.
     */
    public static void cycle(Player player, ItemMenuHostLocator locator, ItemStack itemStack,
            boolean isHandlingRightClick) {
        itemStack.set(AE2wtlibComponents.CURRENT_TERMINAL, nextTerminal(itemStack, isHandlingRightClick));
        updateClientTerminal((ServerPlayer) player, locator, itemStack);
    }

    @Contract(pure = true)
    public static WTDefinition nextTerminal(ItemStack stack, boolean reverse) {
        WTDefinition nextTerminal = WTDefinition.of(stack);
        var terminals = WTDefinition.wirelessTerminalList;
        do {
            int i;
            if (reverse) {
                i = terminals.indexOf(nextTerminal) - 1;
                if (i == -1)
                    i = terminals.size() - 1;
            } else {
                i = terminals.indexOf(nextTerminal) + 1;
                if (i == terminals.size())
                    i = 0;
            }
            nextTerminal = terminals.get(i);
        } while (stack.get(nextTerminal.componentType()) == null);
        return nextTerminal;
    }

    /**
     * Sends an update to the client about the current terminal.
     *
     * @param player  The server player.
     * @param locator The menu locator.
     * @param stack   The compound tag containing terminal data.
     */
    public static void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {
        AE2wtlibAPI.updateClientTerminal(player, locator, stack);
    }

    /**
     * Opens a terminal menu for the player.
     *
     * @param player               The player.
     * @param locator              The menu locator.
     * @param returningFromSubmenu Set to true when the player is returning from a submenu.
     * @return true if the terminal can be opened, false otherwise.
     */
    public static boolean open(final Player player, final ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var currentTerminal = WTDefinition.ofOrNull(locator.locateItem(player));
        if (currentTerminal == null) {
            player.displayClientMessage(TextConstants.TERMINAL_EMPTY, false);
            return false;
        }
        return currentTerminal.containerOpener().tryOpen(player, locator, returningFromSubmenu);
    }

    /**
     * Finds a specific terminal for a player.
     *
     * @param player   The player.
     * @param terminal The name of the terminal to find.
     * @return A MenuLocator for the terminal if found; null otherwise.
     */
    @Nullable
    public static ItemMenuHostLocator findTerminal(Player player, WTDefinition terminal) {
        ItemMenuHostLocator locator = null;

        var cap = player.getCapability(CuriosIntegration.ITEM_HANDLER);
        if (cap != null) {
            for (int i = 0; i < cap.getSlots(); i++) {
                var stack = cap.getStackInSlot(i);
                if (!hasTerminal(stack, terminal))
                    continue;

                if (AE2wtlibAPI.isUniversalTerminal(stack)) {
                    return MenuLocators.forCurioSlot(i);
                } else if (locator == null) {
                    locator = MenuLocators.forCurioSlot(i);
                }
            }
        }

        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            var stack = player.inventory.getItem(i);
            if (!hasTerminal(stack, terminal))
                continue;

            if (AE2wtlibAPI.isUniversalTerminal(stack)) {
                return MenuLocators.forInventorySlot(i);
            } else if (locator == null) {
                locator = MenuLocators.forInventorySlot(i);
            }
        }
        return locator;
    }

    /**
     * Calculates the Maximum UpgradeCardCount based on the known terminals. Each terminal adds two upgrade slots.
     *
     * @return The maximum amount of upgrades a Universal Terminal can have
     */
    @Contract(pure = true)
    public static int getUpgradeCardCount() {
        int upgradeCards = 0;
        for (var terminal : WTDefinition.wirelessTerminals()) {
            upgradeCards += terminal.upgradeCount();
        }
        return upgradeCards;
    }
}
