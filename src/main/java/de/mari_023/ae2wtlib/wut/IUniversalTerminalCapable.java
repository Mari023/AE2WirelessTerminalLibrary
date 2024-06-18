package de.mari_023.ae2wtlib.wut;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.core.network.serverbound.HotkeyPacket;

import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.terminal.ItemButton;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    default Item nextTerminal() {
        return WUTHandler.nextTerminal(getHost().getItemStack(), false).item();
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
    default ItemButton cycleTerminalButton() {
        return new ItemButton(btn -> cycleTerminal(), nextTerminal());
    }
}
