package de.mari_023.ae2wtlib.api.terminal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public record Ae2wtlibLocatingService(WTDefinition terminal) implements HotkeyAction {
    @Override
    public boolean run(Player player) {
        ItemMenuHostLocator locator = WUTHandler.findTerminal(player, terminal);

        if (locator == null)
            return false;

        ItemStack stack = locator.locateItem(player);
        WUTHandler.setCurrentTerminal(player, locator, stack, terminal);
        return terminal.item().tryOpen(player, locator, false);
    }
}
