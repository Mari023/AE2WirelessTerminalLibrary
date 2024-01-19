package de.mari_023.ae2wtlib.wut;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.widgets.ITooltip;
import appeng.core.AppEng;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;

public class CycleTerminalButton extends Button implements ITooltip {
    public CycleTerminalButton(OnPress onPress) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        visible = true;
        active = true;
    }

    @Override
    public @NotNull List<Component> getTooltipMessage() {
        return Collections.singletonList(TextConstants.CYCLE_TOOLTIP);
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), 16, 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return true;
    }

    public static final ResourceLocation TEXTURE_STATES = AppEng.makeId("textures/guis/states.png");
    public static final ResourceLocation nextTerminal = AE2wtlib.id("textures/item/wireless_universal_terminal.png");

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;
        guiGraphics.pose().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        if (isFocused()) {
            guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
        }

        guiGraphics.blit(TEXTURE_STATES, getX(), getY(), 240, 240, 16, 16);

        if (active)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        else
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);

        guiGraphics.blit(nextTerminal, getX() + 1, getY() + 1, 14, 14, 0, 0, 512, 512, 512, 512);

        guiGraphics.pose().popPose();
    }
}
