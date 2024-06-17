package de.mari_023.ae2wtlib.terminal;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ITooltip;

public class ItemButton extends Button implements ITooltip {
    private final ResourceLocation texture;

    public ItemButton(OnPress onPress, ResourceLocation texture) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
    }

    public void setVisibility(final boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, final int mouseX, final int mouseY, float partial) {
        if (!visible)
            return;
        guiGraphics.pose().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        if (!isHovered()) {
            Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(getX() - 1, getY(), 18, 20).blit(guiGraphics);
            guiGraphics.blit(texture, getX(), getY() + 1, 16, 16, 0, 0, 512, 512, 512, 512);
        } else {
            Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER.getBlitter().dest(getX() - 1, getY() + 1, 18, 20)
                    .blit(guiGraphics);
            guiGraphics.blit(texture, getX(), getY() + 2, 16, 16, 0, 0, 512, 512, 512, 512);
        }

        RenderSystem.enableDepthTest();
        guiGraphics.pose().popPose();
    }

    @Override
    public @NotNull List<Component> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), 16, 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }
}
