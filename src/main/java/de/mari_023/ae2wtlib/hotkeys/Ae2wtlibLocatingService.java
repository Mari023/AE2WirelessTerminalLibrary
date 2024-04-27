package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public class Ae2wtlibLocatingService implements HotkeyAction {
    private final String terminalName;

    public Ae2wtlibLocatingService(String terminalName) {
        this.terminalName = terminalName;
    }

    @Override
    public boolean run(Player player) {
        ItemMenuHostLocator locator = WUTHandler.findTerminal(player, terminalName);

        if (locator == null)
            return false;

        ItemStack terminal = locator.locateItem(player);
        WUTHandler.setCurrentTerminal(player, locator, terminal, terminalName);
        return WUTHandler.wirelessTerminals.get(terminalName).item().tryOpen(player, locator, false);
    }
}
