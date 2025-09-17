package de.mari_023.ae2wtlib.terminal;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import appeng.client.gui.widgets.ITooltip;

public class IconButton extends Button implements ITooltip {
    private final Icon icon;
    private final Icon bg;
    @Nullable
    private List<Component> tooltip;

    public IconButton(OnPress onPress, Icon icon) {
        this(onPress, icon, Icon.BUTTON_BACKGROUND);
    }

    public IconButton(OnPress onPress, Icon icon, Icon bg) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.icon = icon;
        this.bg = bg;
    }

    public void setVisibility(boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;

        bg.getBlitter().dest(getX(), getY(), bg.width(), bg.height()).blit(guiGraphics);
        getIcon().getBlitter().dest(getX(), getY()).blit(guiGraphics);

        if (isFocused()) {
            // Draw 1px border with 4 quads
            // top
            guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
            // left
            guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
            // right
            guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
            // bottom
            guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
        }
    }

    protected Icon getIcon() {
        return icon;
    }

    @Override
    public List<Component> getTooltipMessage() {
        if (tooltip == null)
            return Collections.singletonList(getMessage());
        return tooltip;
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(
                getX(),
                getY(),
                bg.width(),
                bg.height());
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    @Contract("_ -> this")
    public IconButton withTooltip(Component message) {
        super.setMessage(message);
        return this;
    }

    @Contract("_ -> this")
    public IconButton withTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
        return this;
    }
}
