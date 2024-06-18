package de.mari_023.ae2wtlib.wet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;

import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

public class WETScreen extends PatternEncodingTermScreen<WETMenu> implements IUniversalTerminalCapable {
    public WETScreen(WETMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(cycleTerminalButton());
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int keyPressed) {
        boolean value = super.keyPressed(keyCode, scanCode, keyPressed);
        if (!value)
            return checkForTerminalKeys(keyCode, scanCode);
        return true;
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }
}
