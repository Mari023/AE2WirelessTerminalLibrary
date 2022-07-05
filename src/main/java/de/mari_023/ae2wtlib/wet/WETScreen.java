package de.mari_023.ae2wtlib.wet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;

public class WETScreen extends PatternEncodingTermScreen<WETMenu> implements IUniversalTerminalCapable {
    public WETScreen(WETMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
    }
}
