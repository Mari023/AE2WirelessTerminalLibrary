package de.mari_023.ae2wtlib.wut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.hotkeys.HotkeyActions;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.curio.CurioHelper;
import de.mari_023.ae2wtlib.hotkeys.Ae2WTLibLocatingService;
import de.mari_023.ae2wtlib.networking.UpdateWUTPackage;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

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
            String currentTerminal = terminal.getOrCreateTag().getString("currentTerminal");

            if (wirelessTerminals.containsKey(currentTerminal))
                return currentTerminal;
            for (String term : terminalNames)
                if (terminal.getOrCreateTag().getBoolean(term)) {
                    currentTerminal = term;
                    terminal.getOrCreateTag().putString("currentTerminal", currentTerminal);
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
    public static void setCurrentTerminal(Player player, MenuLocator locator, ItemStack itemStack,
            String terminal) {
        if (!(itemStack.getItem() instanceof ItemWUT))
            return;
        if (!hasTerminal(itemStack, terminal))
            return;
        assert itemStack.getTag() != null;
        itemStack.getTag().putString("currentTerminal", terminal);
        updateClientTerminal((ServerPlayer) player, locator, itemStack.getTag());
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
            if (terminal.getTag() == null)
                return false;
            return terminal.getTag().getBoolean(terminalName);
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
    public static void cycle(Player player, MenuLocator locator, ItemStack itemStack,
            boolean isHandlingRightClick) {
        if (itemStack.getTag() == null)
            return;
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
        } while (!itemStack.getTag().getBoolean(nextTerminal));
        itemStack.getTag().putString("currentTerminal", nextTerminal);
        updateClientTerminal((ServerPlayer) player, locator, itemStack.getTag());
    }

    /**
     * Sends an update to the client about the current terminal.
     *
     * @param player  The server player.
     * @param locator The menu locator.
     * @param tag     The compound tag containing terminal data.
     */
    public static void updateClientTerminal(ServerPlayer player, MenuLocator locator, @Nullable CompoundTag tag) {
        PacketDistributor.PLAYER.with(player).send(new UpdateWUTPackage(locator, tag));
    }

    /**
     * Opens a terminal menu for the player.
     *
     * @param player               The player.
     * @param locator              The menu locator.
     * @param returningFromSubmenu Set to true when the player is returning from a submenu.
     * @return true if the terminal can be opened, false otherwise.
     */
    public static boolean open(final Player player, final MenuLocator locator, boolean returningFromSubmenu) {
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host == null)
            return false;
        ItemStack is = host.getItemStack();

        if (is.getTag() == null)
            return false;
        String currentTerminal = getCurrentTerminal(is);
        if (!wirelessTerminals.containsKey(currentTerminal)) {
            player.displayClientMessage(TextConstants.TERMINAL_EMPTY, false);
            return false;
        }
        return wirelessTerminals.get(currentTerminal).containerOpener().tryOpen(player, locator, is,
                returningFromSubmenu);
    }

    /**
     * Finds a specific terminal for a player.
     *
     * @param player       The player.
     * @param terminalName The name of the terminal to find.
     * @return A MenuLocator for the terminal if found; null otherwise.
     */
    @Nullable
    public static MenuLocator findTerminal(Player player, String terminalName) {
        MenuLocator locator = CurioHelper.findTerminal(player, terminalName);
        if (locator != null)
            return locator;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (hasTerminal(player.getInventory().getItem(i), terminalName))
                return MenuLocators.forInventorySlot(i);
        }
        return null;
    }

    /**
     * Gets the ItemStack from a locator for a given player.
     *
     * @param player  The player.
     * @param locator The menu locator.
     * @return The ItemStack located, or an empty stack if not found.
     */
    public static ItemStack getItemStackFromLocator(Player player, MenuLocator locator) {
        ItemStack stack = CurioHelper.getItemStack(player, locator);
        if (!stack.isEmpty())
            return stack;
        ItemMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host == null)
            return ItemStack.EMPTY;
        return host.getItemStack();
    }

    /**
     * Registers a new terminal.
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     * @param hotkeyName        The hotkey name for the terminal.
     * @param itemID            The item ID for the terminal.
     */
    public static void addTerminal(String name, WTDefinition.ContainerOpener open,
            WTDefinition.WTMenuHostFactory WTMenuHostFactory,
            MenuType<?> menuType, IUniversalWirelessTerminalItem item, String hotkeyName, String itemID) {
        if (terminalNames.contains(name))
            return;

        ItemStack wut = new ItemStack(AE2wtlibItems.instance().UNIVERSAL_TERMINAL);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(name, true);
        wut.setTag(tag);

        HotkeyActions.register(new Ae2WTLibLocatingService(name), hotkeyName);

        wirelessTerminals.put(name, new WTDefinition(open, WTMenuHostFactory, menuType, item, wut,
                TextConstants.formatTerminalName(itemID)));
        terminalNames.add(name);
    }

    /**
     * Registers a new terminal. Helper for terminals which follow the "wireless_" + name + "_terminal" scheme for
     * hotkeyName
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     * @param itemID            The item ID for the terminal.
     */
    public static void addTerminal(String name, WTDefinition.ContainerOpener open,
            WTDefinition.WTMenuHostFactory WTMenuHostFactory,
            MenuType<?> menuType, IUniversalWirelessTerminalItem item, String itemID) {
        addTerminal(name, open, WTMenuHostFactory, menuType, item, "wireless_" + name + "_terminal", itemID);
    }

    /**
     * Registers a new terminal with the handler. Helper for terminals which follow the "wireless_" + name + "_terminal"
     * scheme for hotkeyName and the "item.ae2wtlib.wireless_" + name + "_terminal" scheme for the itemID
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     */
    public static void addTerminal(String name, WTDefinition.ContainerOpener open,
            WTDefinition.WTMenuHostFactory WTMenuHostFactory,
            MenuType<?> menuType, IUniversalWirelessTerminalItem item) {
        addTerminal(name, open, WTMenuHostFactory, menuType, item, "item.ae2wtlib.wireless_" + name + "_terminal");
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
