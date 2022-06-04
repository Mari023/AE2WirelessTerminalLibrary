package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetSettings;

import appeng.api.features.HotkeyAction;

public class MagnetHotkeyAction implements HotkeyAction {
    @Override
    public boolean run(Player player) {
        ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(player)
                .getCraftingTerminal();
        if (terminal.isEmpty())
            return false;
        MagnetSettings settings = MagnetHandler.getMagnetSettings(terminal);
        settings.magnetMode = switch (settings.magnetMode) {
            case OFF -> {
                player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_INVENTORY, true);
                yield MagnetMode.PICKUP_INVENTORY;
            }
            case PICKUP_INVENTORY -> {
                player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_ME, true);
                yield MagnetMode.PICKUP_ME;
            }
            case PICKUP_ME -> {
                player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_OFF, true);
                yield MagnetMode.OFF;
            }
            default -> settings.magnetMode;
        };
        MagnetHandler.saveMagnetSettings(terminal, settings);
        return true;
    }
}
