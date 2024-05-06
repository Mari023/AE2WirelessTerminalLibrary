package de.mari_023.ae2wtlib.wut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import appeng.api.config.Actionable;
import appeng.hotkeys.HotkeyActions;
import appeng.integration.modules.curios.CuriosIntegration;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.AE2wtlibComponents;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.hotkeys.Ae2wtlibLocatingService;
import de.mari_023.ae2wtlib.networking.UpdateWUTPackage;
import de.mari_023.ae2wtlib.terminal.ItemWT;

/**
 * This class handles functionalities related to the Wireless Universal Terminal (WUT). It provides methods to get and
 * set the current terminal, check for a specific terminal, cycle through terminals, update client terminals, and
 * register new terminals.
 */
public class WUTHandler {
    /**
     * Definitions for all wireless terminals.
     */
    public static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();
    public static final List<String> terminalNames = new ArrayList<>();

    /**
     * Get the current terminal from the provided item stack.
     *
     * @param terminal The ItemStack which represents the terminal.
     * @return The current terminal name if found, or an empty string.
     */
    public static String getCurrentTerminal(ItemStack terminal) {
        if (terminal.getItem() instanceof ItemWUT) {
            String currentTerminal = terminal.getOrDefault(AE2wtlibComponents.CURRENT_TERMINAL, "");

            if (wirelessTerminals.containsKey(currentTerminal))
                return currentTerminal;
            for (var term : wirelessTerminals.entrySet())
                if (terminal.get(term.getValue().componentType()) != null) {
                    currentTerminal = term.getKey();
                    terminal.set(AE2wtlibComponents.CURRENT_TERMINAL, currentTerminal);
                    break;
                }
            return currentTerminal;
        }
        for (Map.Entry<String, WTDefinition> entry : wirelessTerminals.entrySet()) {
            if (terminal.getItem().equals(entry.getValue().item()))
                return entry.getKey();
        }
        return "";
    }

    /**
     * Set the current terminal of a given item stack.
     *
     * @param player    The player.
     * @param locator   The menu locator.
     * @param itemStack The ItemStack to update.
     * @param terminal  The terminal name to set.
     */
    public static void setCurrentTerminal(Player player, ItemMenuHostLocator locator, ItemStack itemStack,
            String terminal) {
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
     * @param terminal     The terminal ItemStack.
     * @param terminalName The terminal name to check.
     * @return true if the terminal contains the specified terminal name, false otherwise.
     */
    public static boolean hasTerminal(ItemStack terminal, String terminalName) {
        if (terminal.isEmpty())
            return false;
        if (terminal.getItem() instanceof ItemWUT) {
            if (!terminalNames.contains(terminalName))
                return false;
            return terminal.get(wirelessTerminals.get(terminalName).componentType()) != null;
        }
        return terminal.getItem().equals(wirelessTerminals.get(terminalName).item());
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
        String nextTerminal = getCurrentTerminal(itemStack);
        do {
            int i;
            if (isHandlingRightClick) {
                i = terminalNames.indexOf(nextTerminal) - 1;
                if (i == -1)
                    i = terminalNames.size() - 1;
            } else {
                i = terminalNames.indexOf(nextTerminal) + 1;
                if (i == terminalNames.size())
                    i = 0;
            }
            nextTerminal = terminalNames.get(i);
        } while (itemStack.get(wirelessTerminals.get(nextTerminal).componentType()) == null);
        itemStack.set(AE2wtlibComponents.CURRENT_TERMINAL, nextTerminal);
        updateClientTerminal((ServerPlayer) player, locator, itemStack);
    }

    /**
     * Sends an update to the client about the current terminal.
     *
     * @param player  The server player.
     * @param locator The menu locator.
     * @param stack   The compound tag containing terminal data.
     */
    public static void updateClientTerminal(ServerPlayer player, ItemMenuHostLocator locator, ItemStack stack) {
        PacketDistributor.sendToPlayer(player, new UpdateWUTPackage(locator, stack));
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
        ItemStack is = locator.locateItem(player);

        String currentTerminal = getCurrentTerminal(is);
        if (!wirelessTerminals.containsKey(currentTerminal)) {
            player.displayClientMessage(TextConstants.TERMINAL_EMPTY, false);
            return false;
        }
        return wirelessTerminals.get(currentTerminal).containerOpener().tryOpen(player, locator, returningFromSubmenu);
    }

    /**
     * Finds a specific terminal for a player.
     *
     * @param player       The player.
     * @param terminalName The name of the terminal to find.
     * @return A MenuLocator for the terminal if found; null otherwise.
     */
    @Nullable
    public static ItemMenuHostLocator findTerminal(Player player, String terminalName) {
        var cap = player.getCapability(CuriosIntegration.ITEM_HANDLER);
        if (cap != null) {
            for (int i = 0; i < cap.getSlots(); i++) {
                if (hasTerminal(cap.getStackInSlot(i), terminalName)) {
                    return MenuLocators.forCurioSlot(i);
                }
            }
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (hasTerminal(player.getInventory().getItem(i), terminalName))
                return MenuLocators.forInventorySlot(i);
        }
        return null;
    }

    /**
     * Calculates the Maximum UpgradeCardCount based on the known terminals. Each terminal adds two upgrade slots.
     *
     * @return The maximum amount of upgrades a Universal Terminal can have
     */
    public static int getUpgradeCardCount() {
        return terminalNames.size() * 2;
    }
}
