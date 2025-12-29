package de.mari_023.ae2wtlib.wet;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;

import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

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
    public boolean keyPressed(KeyEvent event) {
        boolean value = super.keyPressed(event);
        if (!value)
            return checkForTerminalKeys(event);
        return true;
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }
}
