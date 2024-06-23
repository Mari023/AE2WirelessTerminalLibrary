package de.mari_023.ae2wtlib.wut;

import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.core.network.serverbound.HotkeyPacket;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.terminal.Icon;
import de.mari_023.ae2wtlib.terminal.IconButton;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    default Icon nextTerminal() {
        return WUTHandler.nextTerminal(getHost().getItemStack(), false).icon();
    }

    WTMenuHost getHost();

    boolean isHandlingRightClick();

    void storeState();

    default boolean checkForTerminalKeys(int keyCode, int scanCode) {
        for (var terminal : WTDefinition.wirelessTerminals()) {
            var hotkey = Hotkeys.getHotkeyMapping(terminal.hotkeyName());
            if (hotkey == null)
                continue;
            if (hotkey.mapping().matches(keyCode, scanCode)) {
                PacketDistributor.sendToServer(new HotkeyPacket(hotkey));
                return true;
            }
        }
        return false;
    }

    /**
     * creates the button that switches to the next terminal. you are responsible for adding this to the leftToolbar
     * when appropriate
     * 
     * @return CycleTerminalButton
     */
    default IconButton cycleTerminalButton() {
        return IconButton.withAE2Background(btn -> cycleTerminal(), nextTerminal())
                .withMessage(TextConstants.CYCLE_TOOLTIP);
    }
}
