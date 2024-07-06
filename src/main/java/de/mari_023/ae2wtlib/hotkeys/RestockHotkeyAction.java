package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class RestockHotkeyAction implements HotkeyAction {
    @Override
    public boolean run(Player player) {
        CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler
                .getCraftingTerminalHandler(player);
        ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
        if (terminal.isEmpty())
            return false;
        if (terminal.getItem() instanceof ItemWT) {
            terminal.set(AE2wtlibComponents.RESTOCK, !terminal.getOrDefault(AE2wtlibComponents.RESTOCK, false));
        }
        ItemMenuHostLocator locator = craftingTerminalHandler.getLocator();
        if (locator != null)
            WUTHandler.updateClientTerminal((ServerPlayer) player, locator, terminal);

        if (terminal.getOrDefault(AE2wtlibComponents.RESTOCK, false))
            player.displayClientMessage(TextConstants.RESTOCK_ON, true);
        else
            player.displayClientMessage(TextConstants.RESTOCK_OFF, true);

        return true;
    }
}
