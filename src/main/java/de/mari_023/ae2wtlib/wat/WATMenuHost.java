package de.mari_023.ae2wtlib.wat;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class WATMenuHost extends WTMenuHost {
    public WATMenuHost(final Player ep, ItemMenuHostLocator locator, final ItemStack is,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, locator, is, returnToMainMenu);
        readFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlibItems.instance().PATTERN_ACCESS_TERMINAL);
    }
}
