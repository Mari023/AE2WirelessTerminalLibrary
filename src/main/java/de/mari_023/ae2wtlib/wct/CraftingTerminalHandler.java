package de.mari_023.ae2wtlib.wct;

import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;

import appeng.api.networking.security.IActionHost;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.Platform;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.menu.locator.MenuLocator;

public class CraftingTerminalHandler {

    private static final WeakHashMap<UUID, CraftingTerminalHandler> players = new WeakHashMap<>();
    private final Player player;
    private ItemStack craftingTerminal = ItemStack.EMPTY;
    private WCTMenuHost menuHost;
    private MenuLocator locator;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();
    private MagnetHost magnetHost;

    private CraftingTerminalHandler(Player player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(Player player) {
        if (players.containsKey(player.getUUID())) {
            if (player == players.get(player.getUUID()).player ||
                    (!(player instanceof ServerPlayer)
                            && (players.get(player.getUUID()).player instanceof ServerPlayer)))
                return players.get(player.getUUID());
            removePlayer(player);
        }
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player.getUUID(), handler);
        return handler;
    }

    public static void removePlayer(Player player) {
        players.remove(player.getUUID());
    }

    public void invalidateCache() {
        craftingTerminal = ItemStack.EMPTY;
        menuHost = null;
        locator = null;
        restockAbleItems.clear();
        magnetHost = null;
    }

    public ItemStack getCraftingTerminal() {
        Inventory inv = player.getInventory();
        if ((!craftingTerminal.isEmpty()) && (inv.contains(craftingTerminal)
                || (Platform.trinketsPresent()
                && Platform.isStillPresentTrinkets(player, craftingTerminal))))
            return craftingTerminal;


        if (getMenuHost() == null)
            craftingTerminal = ItemStack.EMPTY;
        else
            craftingTerminal = menuHost.getItemStack();


        return craftingTerminal;
    }

    @Nullable
    public WCTMenuHost getMenuHost() {
        if (menuHost != null && menuHost.rangeCheck() && menuHost.stillValid()) {
            return menuHost;
        }

        locator = WUTHandler.findTerminal(player, "crafting");

        if (locator == null)
            menuHost = null;
        else
            menuHost = locator.locate(player, WCTMenuHost.class);

        if (menuHost == null) {
            invalidateCache();
        }

        return menuHost;
    }

    public IActionHost getSecurityStation() {
        return menuHost.getActionHost();
    }

    @Nullable
    public MenuLocator getLocator() {
        if (getMagnetHost() == null)
            return null;
        return locator;
    }

    @Nullable
    public IGrid getTargetGrid() {
        final IGridNode n = menuHost.getActionableNode();

        if (n == null)
            return null;
        return menuHost.getActionableNode().getGrid();
    }

    public boolean inRange() {
        if (getMenuHost() == null) return false;
        return getMenuHost().rangeCheck();
    }

    public long getAccessibleAmount(ItemStack stack) {
        return stack.getCount()
                + (restockAbleItems.get(stack.getItem()) == null ? 0 : restockAbleItems.get(stack.getItem()));
    }

    public boolean isRestockable(ItemStack stack) {
        return restockAbleItems.containsKey(stack.getItem());
    }

    public void setRestockAbleItems(HashMap<Item, Long> items) {
        restockAbleItems = items;
    }

    @Nullable
    public MagnetHost getMagnetHost() {
        if (magnetHost == null) {
            if (getCraftingTerminal().isEmpty())
                return null;
            magnetHost = new MagnetHost(this);
        }
        return magnetHost;
    }
}
