package de.mari_023.ae2wtlib.wat;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;

import appeng.api.networking.IGridNode;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class WATMenuHost extends WTMenuHost implements IPatternAccessTermMenuHost {
    public WATMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }

    @Override
    public @Nullable IGridNode getGridNode() {
        return getActionableNode();
    }
}
