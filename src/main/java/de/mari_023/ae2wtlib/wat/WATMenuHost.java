package de.mari_023.ae2wtlib.wat;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.networking.IGridNode;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class WATMenuHost extends WTMenuHost implements IPatternAccessTermMenuHost {
    public WATMenuHost(WirelessTerminalItem item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlibItems.instance().PATTERN_ACCESS_TERMINAL);
    }

    @Override
    public @Nullable IGridNode getGridNode() {
        return getActionableNode();
    }
}
