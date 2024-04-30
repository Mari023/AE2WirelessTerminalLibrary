package de.mari_023.ae2wtlib.wut;

import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.core.network.serverbound.HotkeyPacket;

import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    boolean isHandlingRightClick();

    void storeState();

    default boolean checkForTerminalKeys(int keyCode, int scanCode) {
        for (var wtDefinition : WUTHandler.wirelessTerminals.values()) {
            var hotkey = Hotkeys.getHotkeyMapping(wtDefinition.hotkeyName());
            if (hotkey == null)
                continue;
            if (hotkey.mapping().matches(keyCode, scanCode)) {
                PacketDistributor.sendToServer(new HotkeyPacket(hotkey));
                return true;
            }
        }
        return false;
    }
}
