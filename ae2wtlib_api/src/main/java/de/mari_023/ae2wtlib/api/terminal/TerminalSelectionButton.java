package de.mari_023.ae2wtlib.api.terminal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import appeng.client.gui.style.BackgroundGenerator;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.gui.IconButton;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

/**
 * Provides a button that opens a terminal selector panel for the wireless universal terminal.
 */
final class TerminalSelectionButton extends IconButton {
    private static final int BUTTON_WIDTH = 18;
    private static final int BUTTON_HEIGHT = 20;
    private static final int MAX_ROWS = 3;
    private static final int GAP = 2;
    private static final int PANEL_GAP = 5;
    private static final int PANEL_PADDING = 5;
    private static final int ICON_OFFSET = 1;
    private static final int TOOLBAR_BUTTON_SPACING = 6;

    private final WTMenuHost host;
    private final List<WTDefinition> terminals;
    private final Runnable storeState;
    private boolean menuOpen;
    private List<Component> tooltip = List.of();

    TerminalSelectionButton(WTMenuHost host, Runnable storeState) {
        super(btn -> {
        }, Icon.CRAFTING, Icon.TOOLBAR_BUTTON_BACKGROUND, Icon.TOOLBAR_BUTTON_BACKGROUND_HOVERED,
                Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUSED);
        this.host = host;
        this.storeState = storeState;
        this.terminals = installedTerminals(host);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;

        boolean alwaysVisible = alwaysVisible();
        tooltip = List.of();
        if (!alwaysVisible) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partial);
            if (isHovered())
                tooltip = currentTooltip();
        }

        if (menuOpen || alwaysVisible)
            renderMenu(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected Icon getIcon() {
        WTDefinition terminal = WTDefinition.ofOrNull(host.getItemStack());
        return terminal == null ? Icon.CRAFTING : terminal.icon();
    }

    @Override
    public void onPress() {
        if (!alwaysVisible() && !terminals.isEmpty())
            menuOpen = !menuOpen;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean alwaysVisible = alwaysVisible();
        if ((menuOpen || alwaysVisible) && button == 0) {
            WTDefinition terminal = terminalAt(mouseX, mouseY);
            if (terminal != null) {
                if (!alwaysVisible)
                    menuOpen = false;
                storeState.run();
                AE2wtlibAPI.selectTerminal(terminal);
                return true;
            }
        }

        if (alwaysVisible)
            return false;

        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        if (menuOpen) {
            menuOpen = false;
        }
        return false;
    }

    @Override
    public int getWidth() {
        return alwaysVisible() ? 0 : super.getWidth();
    }

    @Override
    public int getHeight() {
        return alwaysVisible() ? -TOOLBAR_BUTTON_SPACING : super.getHeight();
    }

    @Override
    public List<Component> getTooltipMessage() {
        return tooltip;
    }

    @Override
    public Rect2i getTooltipArea() {
        if (!menuOpen && !alwaysVisible())
            return super.getTooltipArea();

        int x = panelX() - 1;
        int y = panelY() - 1;
        int right = Math.max(getX() + getWidth(), panelX() + panelWidth() + 1);
        int bottom = Math.max(getY() + getHeight(), panelY() + panelHeight() + 1);
        return new Rect2i(x, y, right - x, bottom - y);
    }

    private static List<WTDefinition> installedTerminals(WTMenuHost host) {
        List<WTDefinition> terminals = new ArrayList<>();
        for (var terminal : WTDefinition.wirelessTerminalList) {
            if (WUTHandler.hasTerminal(host.getItemStack(), terminal))
                terminals.add(terminal);
        }
        return terminals;
    }

    private boolean alwaysVisible() {
        return AE2wtlibAPI.alwaysShowTerminalSelector() && !terminals.isEmpty();
    }

    private List<Component> currentTooltip() {
        WTDefinition terminal = WTDefinition.ofOrNull(host.getItemStack());
        if (terminal == null)
            return List.of(TextConstants.TERMINAL_EMPTY);
        return List.of(TextConstants.currentTerminal(terminal));
    }

    private void renderMenu(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int panelX = panelX();
        int panelY = panelY();
        int width = panelWidth();
        int height = panelHeight();
        WTDefinition currentTerminal = WTDefinition.ofOrNull(host.getItemStack());

        BackgroundGenerator.draw(width, height, guiGraphics, panelX, panelY);

        for (int i = 0; i < terminals.size(); i++) {
            WTDefinition terminal = terminals.get(i);
            int x = buttonX(i);
            int y = buttonY(i);
            boolean selected = terminal.equals(currentTerminal);
            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + BUTTON_WIDTH && mouseY < y + BUTTON_HEIGHT;
            int yOffset = hovered ? 1 : 0;
            Icon background = hovered ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVERED
                    : selected ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUSED : Icon.TOOLBAR_BUTTON_BACKGROUND;

            background.getBlitter().dest(x, y + yOffset, background.width(), background.height()).zOffset(2)
                    .blit(guiGraphics);
            terminal.icon().getBlitter().dest(x + ICON_OFFSET, y + ICON_OFFSET + yOffset).zOffset(3).blit(guiGraphics);

            if (hovered)
                tooltip = List.of(terminal.formattedName());
        }
    }

    private WTDefinition terminalAt(double mouseX, double mouseY) {
        for (int i = 0; i < terminals.size(); i++) {
            int x = buttonX(i);
            int y = buttonY(i);
            if (mouseX >= x && mouseY >= y && mouseX < x + BUTTON_WIDTH && mouseY < y + BUTTON_HEIGHT)
                return terminals.get(i);
        }
        return null;
    }

    private int panelX() {
        int x = getX();
        if (alwaysVisible())
            x -= super.getWidth();
        return x - PANEL_GAP - panelWidth();
    }

    private int panelY() {
        return getY();
    }

    private int panelWidth() {
        return PANEL_PADDING * 2 + columns() * BUTTON_WIDTH + Math.max(0, columns() - 1) * GAP;
    }

    private int panelHeight() {
        return PANEL_PADDING * 2 + rows() * BUTTON_HEIGHT + Math.max(0, rows() - 1) * GAP;
    }

    private int buttonX(int index) {
        return panelX() + PANEL_PADDING + (index % columns()) * (BUTTON_WIDTH + GAP);
    }

    private int buttonY(int index) {
        return panelY() + PANEL_PADDING + (index / columns()) * (BUTTON_HEIGHT + GAP);
    }

    private int columns() {
        return Math.max(1, (terminals.size() + MAX_ROWS - 1) / MAX_ROWS);
    }

    private int rows() {
        return Math.max(1, (terminals.size() + columns() - 1) / columns());
    }
}
