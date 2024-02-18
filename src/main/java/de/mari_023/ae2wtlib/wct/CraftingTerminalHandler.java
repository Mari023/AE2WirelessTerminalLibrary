package de.mari_023.ae2wtlib.wct;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wut.WUTHandler;

public class CraftingTerminalHandler {
    private static final WeakHashMap<Player, CraftingTerminalHandler> SERVER_PLAYERS = new WeakHashMap<>();
    private static final WeakHashMap<Player, CraftingTerminalHandler> CLIENT_PLAYERS = new WeakHashMap<>();
    private final Player player;
    @Nullable
    private WTMenuHost menuHost;
    @Nullable
    private ItemMenuHostLocator locator;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();
    @Nullable
    private MagnetHost magnetHost;

    private CraftingTerminalHandler(Player player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(Player player) {
        Map<Player, CraftingTerminalHandler> players = player instanceof ServerPlayer ? SERVER_PLAYERS : CLIENT_PLAYERS;

        if (players.containsKey(player)) {
            if (player == players.get(player).player)
                return players.get(player);
            removePlayer(player);
        }
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player, handler);
        return handler;
    }

    public static void removePlayer(Player player) {
        if (player instanceof ServerPlayer)
            SERVER_PLAYERS.remove(player);
        else
            CLIENT_PLAYERS.remove(player);
    }

    private void invalidateCache() {
        menuHost = null;
        locator = null;
        restockAbleItems.clear();
        magnetHost = null;
    }

    public ItemStack getCraftingTerminal() {
        getLocator();
        if (locator != null)
            return locator.locateItem(player);
        return ItemStack.EMPTY;
    }

    @Nullable
    private WTMenuHost getMenuHost() {
        if (menuHost != null) {
            if (!menuHost.isValid()) {
                invalidateCache();
                return getMenuHost();
            }
            return menuHost;
        }

        getLocator();

        if (locator == null)
            menuHost = null;
        else
            menuHost = locator.locate(player, WTMenuHost.class);

        if (menuHost == null) {
            invalidateCache();
        }

        return menuHost;
    }

    @Nullable
    public ItemMenuHostLocator getLocator() {
        if (locator != null && WUTHandler.hasTerminal(locator.locateItem(player), "crafting"))
            return locator;
        boolean locatorWasNotNull = locator != null;
        locator = WUTHandler.findTerminal(player, "crafting");

        if (locator == null) {
            invalidateCache();
            if (locatorWasNotNull)
                return getLocator();
        }
        return locator;
    }

    @Nullable
    public IGrid getTargetGrid() {
        if (getMenuHost() == null)
            return null;
        final IGridNode n = getMenuHost().getActionableNode();

        if (n == null)
            return null;
        return n.getGrid();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inRange() {
        if (getMenuHost() == null)
            return false;

        getMenuHost().updateConnectedAccessPoint();
        getMenuHost().updateLinkStatus();
        return getMenuHost().getLinkStatus().connected();
    }

    public long getAccessibleAmount(ItemStack stack) {
        return stack.getCount()
                + (restockAbleItems.get(stack.getItem()) == null ? 0 : restockAbleItems.get(stack.getItem()));
    }

    public boolean isRestockAble(ItemStack stack) {
        return restockAbleItems.containsKey(stack.getItem());
    }

    public void setRestockAbleItems(HashMap<Item, Long> items) {
        restockAbleItems = items;
    }

    @Nullable
    public MagnetHost getMagnetHost() {
        if (magnetHost == null) {
            if (getLocator() == null)
                return magnetHost = null;
            magnetHost = new MagnetHost(this);
        }
        return magnetHost;
    }
}
