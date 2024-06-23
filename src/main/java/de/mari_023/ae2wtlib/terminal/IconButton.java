package de.mari_023.ae2wtlib.terminal;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import appeng.client.gui.widgets.ITooltip;

public class IconButton extends Button implements ITooltip {
    private final Icon icon;
    private final Icon bg;
    private final Icon bg_hovered;

    public IconButton(OnPress onPress, Icon icon) {
        this(onPress, icon, Icon.BUTTON_BACKGROUND, Icon.BUTTON_BACKGROUND_HOVER);
    }

    public IconButton(OnPress onPress, Icon icon, Icon bg, Icon bg_hovered) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.icon = icon;
        this.bg = bg;
        this.bg_hovered = bg_hovered;
    }

    public static IconButton withAE2Background(OnPress onPress, Icon icon) {
        return new IconButton(onPress, icon, Icon.TOOLBAR_BUTTON_BACKGROUND, Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER);
    }

    public void setVisibility(boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;

        var yOffset = isHoveredOrFocused() ? 1 : 0;
        var bg = getBG();
        var bgSizeOffset = bg.width > 16 ? 1 : 0;

        bg.getBlitter()
                .dest(getX() - 1, getY() + yOffset, bg.width, bg.height)
                .zOffset(2)
                .blit(guiGraphics);
        getIcon().getBlitter().dest(getX() - 1 + bgSizeOffset, getY() + bgSizeOffset + yOffset).zOffset(3)
                .blit(guiGraphics);
    }

    protected Icon getIcon() {
        return icon;
    }

    private Icon getBG() {
        return isHoveredOrFocused() ? bg_hovered : bg;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(
                getX(),
                getY(),
                getBG().width,
                getBG().height);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    public IconButton withMessage(Component message) {
        super.setMessage(message);
        return this;
    }
}
