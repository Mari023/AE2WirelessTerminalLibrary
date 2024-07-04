package de.mari_023.ae2wtlib.wet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;

import de.mari_023.ae2wtlib.terminal.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

public class WETScreen extends PatternEncodingTermScreen<WETMenu> implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;

    public WETScreen(WETMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(cycleTerminalButton());
        upgradesPanel = addUpgradePanel(widgets, getMenu());
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
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
