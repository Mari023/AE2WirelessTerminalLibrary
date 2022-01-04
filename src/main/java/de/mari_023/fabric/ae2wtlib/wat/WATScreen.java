package de.mari_023.fabric.ae2wtlib.wat;

import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.io.IOException;

public class WATScreen extends InterfaceTerminalScreen<WATMenu> implements IUniversalTerminalCapable {

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle style;
        try {
            style = StyleManager.loadStyleDoc("/screens/wtlib/wireless_pattern_access_terminal.json");
        } catch(IOException ignored) {
            style = null;
        }
        STYLE = style;
    }

    public WATScreen(WATMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, STYLE);
        if(getMenu().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));
    }
}