package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.MenuLocator;

public class Ae2WTLibLocatingService implements HotkeyAction {

    private final String terminalName;

    public Ae2WTLibLocatingService(String terminalName) {
        this.terminalName = terminalName;
    }

    @Override
    public boolean run(Player player) {
        MenuLocator locator = WUTHandler.findTerminal(player, terminalName);

        if (locator == null)
            return false;

        ItemStack terminal = WUTHandler.getItemStackFromLocator(player, locator);
        WUTHandler.setCurrentTerminal(player, locator, terminal, terminalName);
        return WUTHandler.wirelessTerminals.get(terminalName).item().tryOpen(player, locator, terminal);
    }
}
