package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.MenuLocator;

public class RestockHotkeyAction implements HotkeyAction {
    @Override
    public boolean run(Player player) {
        CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler
                .getCraftingTerminalHandler(player);
        ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
        if (terminal.isEmpty())
            return false;
        ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
        MenuLocator locator = craftingTerminalHandler.getLocator();
        if (locator != null)
            WUTHandler.updateClientTerminal((ServerPlayer) player, locator, terminal.getTag());

        if (ItemWT.getBoolean(terminal, "restock"))
            player.displayClientMessage(TextConstants.RESTOCK_ON, true);
        else
            player.displayClientMessage(TextConstants.RESTOCK_OFF, true);

        return true;
    }
}
