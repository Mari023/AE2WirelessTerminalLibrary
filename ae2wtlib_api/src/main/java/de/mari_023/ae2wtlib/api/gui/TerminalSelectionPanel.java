package de.mari_023.ae2wtlib.api.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Rects;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.VerticalButtonBar;
import appeng.core.network.serverbound.HotkeyPacket;

import de.mari_023.ae2wtlib.api.mixin.WidgetContainerAccessor;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;

public class TerminalSelectionPanel implements ICompositeWidget {
    private static final int BUTTON_WIDTH = Icon.TOOLBAR_BUTTON_BACKGROUND.width();
    private static final int BUTTON_HEIGHT = Icon.TOOLBAR_BUTTON_BACKGROUND.height();
    private static final int MAX_ROWS = 3;
    private static final int GAP = 2;
    private static final int PADDING = 3;

    // The screen origin in window space
    private Point screenOrigin = Point.ZERO;

    private final List<WTDefinition> terminals;
    private final WidgetContainer widgets;
    private final List<IconButton> buttons = new ArrayList<>();

    public TerminalSelectionPanel(WTMenuHost host, WidgetContainer widgets) {
        terminals = installedTerminals(host);
        this.widgets = widgets;
        for (var terminal : terminals) {
            IconButton button = IconButton.withAE2Background((_) -> {
                var hotkey = Hotkeys.getHotkeyMapping(terminal.hotkeyName());
                if (hotkey == null)
                    return;
                ClientPacketDistributor.sendToServer(new HotkeyPacket(hotkey));
            }, terminal.icon()).withTooltip(terminal.formattedName());
            buttons.add(button);
        }
    }

    @Override
    public void updateBeforeRender() {
        for (int i = 0; i < buttons.size(); i++) {
            IconButton button = buttons.get(i);
            button.setPosition(buttonX(i, buttons.size()), buttonY(i, buttons.size()));
        }
    }

    @Override
    public void setPosition(Point position) {
    }

    @Override
    public void setSize(int width, int height) {
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x(), y(), width(), height());
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        screenOrigin = Point.fromTopLeft(bounds);
        for (int i = 0; i < buttons.size(); i++) {
            IconButton button = buttons.get(i);
            addWidget.accept(button);
            button.setPosition(buttonX(i, buttons.size()), buttonY(i, buttons.size()));
        }
    }

    private static List<WTDefinition> installedTerminals(WTMenuHost host) {
        List<WTDefinition> terminals = new ArrayList<>();
        for (var terminal : WTDefinition.wirelessTerminalList) {
            if (WUTHandler.hasTerminal(host.getItemStack(), terminal))
                terminals.add(terminal);
        }
        return terminals;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor guiGraphics, Rect2i bounds, Point mouse) {
        Icon.TERMINAL_SWITCHER.blit(guiGraphics, x() - PADDING, y() - PADDING);
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        // Use a bit of a margin around the zone to avoid things looking too cramped
        exclusionZones.add(Rects.expand(getBounds(), PADDING));
    }

    private int x() {
        VerticalButtonBar toolbar = (VerticalButtonBar) ((WidgetContainerAccessor) widgets).getCompositeWidgets()
                .get("verticalToolbar");
        if (toolbar == null) {
            return screenOrigin.getX();
        }
        return screenOrigin.getX() + toolbar.getBounds().getX() + 1 - (columns() - 1) * (BUTTON_WIDTH + GAP);
    }

    private int y() {
        VerticalButtonBar toolbar = (VerticalButtonBar) ((WidgetContainerAccessor) widgets).getCompositeWidgets()
                .get("verticalToolbar");
        if (toolbar == null) {
            return screenOrigin.getY();
        }
        return screenOrigin.getY() + toolbar.getBounds().getY() + toolbar.getBounds().getHeight();
    }

    private int width() {
        return columns() * (BUTTON_WIDTH + GAP) - GAP;
    }

    private int height() {
        return Math.min(rows(), MAX_ROWS) * (BUTTON_HEIGHT + GAP) - GAP;
    }

    private int buttonX(int i, int count) {
        int overflow = count % MAX_ROWS;
        if (i >= overflow && overflow != 0) {
            i += MAX_ROWS - overflow;
        }
        return x() + 1 + (BUTTON_WIDTH + GAP) * (i / MAX_ROWS);
    }

    private int buttonY(int i, int count) {
        int overflow = count % MAX_ROWS;
        if (i >= overflow && overflow != 0) {
            i += MAX_ROWS - overflow;
        }
        return y() + (BUTTON_HEIGHT + GAP) * (i % MAX_ROWS);
    }

    private int columns() {
        return Math.max(1, (terminals.size() + rows() - 1) / rows());
    }

    private int rows() {
        return Math.max(1, Math.min(MAX_ROWS, terminals.size()));
    }
}
