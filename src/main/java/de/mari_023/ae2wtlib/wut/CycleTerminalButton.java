package de.mari_023.ae2wtlib.wut;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;

import appeng.client.gui.widgets.ITooltip;

public class CycleTerminalButton extends Button implements ITooltip {

    public CycleTerminalButton(OnPress onPress) {
        super(0, 0, 16, 16, TextConstants.CYCLE, onPress);
        visible = true;
        active = true;
    }

    @Override
    public @NotNull List<Component> getTooltipMessage() {
        return Collections.singletonList(TextConstants.CYCLE_TOOLTIP);
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(x, y, 16, 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return true;
    }

    public static final ResourceLocation TEXTURE_STATES = new ResourceLocation("ae2", "textures/guis/states.png");
    public static final ResourceLocation nextTerminal = new ResourceLocation(AE2wtlib.MOD_NAME,
            "textures/wireless_universal_terminal.png");

    @Override
    public void renderButton(PoseStack matrices, final int mouseX, final int mouseY, float partial) {
        if (!visible)
            return;
        matrices.pushPose();
        RenderSystem.setShaderTexture(0, TEXTURE_STATES);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        if (isFocused()) {
            fill(matrices, x - 1, y - 1, x + width + 1, y + height + 1, 0xFFFFFFFF);
        }

        blit(matrices, x, y, 240, 240, 16, 16);

        RenderSystem.setShaderTexture(0, nextTerminal);

        if (active)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        else
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);

        blit(matrices, x + 1, y + 1, 14, 14, 0, 0, 512, 512, 512, 512);

        matrices.popPose();
        if (isHoveredOrFocused())
            renderToolTip(matrices, mouseX, mouseY);
    }
}
