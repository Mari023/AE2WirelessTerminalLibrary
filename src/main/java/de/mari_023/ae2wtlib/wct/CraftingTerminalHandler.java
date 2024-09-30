package de.mari_023.ae2wtlib.wct;

import java.util.HashMap;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

public class CraftingTerminalHandler {
    public final Player player;
    @Nullable
    private WTMenuHost menuHost;
    @Nullable
    private ItemMenuHostLocator locator;
    @Nullable
    private MagnetHost magnetHost;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();
    private boolean restockEnabled = false;

    @ApiStatus.Internal
    public CraftingTerminalHandler(Player player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(Player player) {
        return player.getData(AE2wtlibAdditionalComponents.CT_HANDLER);
    }

    protected void invalidateCache() {
        menuHost = null;
        locator = null;
        magnetHost = null;
        restockAbleItems.clear();
        restockEnabled = false;
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
        if (locator != null && WUTHandler.hasTerminal(locator.locateItem(player), WTDefinitions.CRAFTING))
            return locator;
        boolean locatorWasNotNull = locator != null;
        locator = WUTHandler.findTerminal(player, WTDefinitions.CRAFTING);

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

    @Nullable
    public MagnetHost getMagnetHost() {
        if (magnetHost == null) {
            if (getLocator() == null)
                return magnetHost = null;
            magnetHost = new MagnetHost(this);
        }
        return magnetHost;
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

    public boolean isRestockEnabled() {
        return restockEnabled;
    }

    public void checkTerminal() {
        restockEnabled = getCraftingTerminal().getOrDefault(AE2wtlibComponents.RESTOCK, false);
    }
}
