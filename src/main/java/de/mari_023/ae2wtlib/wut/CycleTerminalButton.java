package de.mari_023.ae2wtlib.wut;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ITooltip;

import de.mari_023.ae2wtlib.TextConstants;

public class CycleTerminalButton extends Button implements ITooltip {
    private final ItemStack nextTerminal;

    public CycleTerminalButton(IUniversalTerminalCapable terminalScreen) {
        super(0, 0, 16, 16, Component.empty(), btn -> terminalScreen.cycleTerminal(), Button.DEFAULT_NARRATION);
        this.nextTerminal = terminalScreen.nextTerminal();
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

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;
        guiGraphics.pose().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        if (!isHovered()) {
            Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(getX() - 1, getY(), 18, 20).blit(guiGraphics);
            renderScaledItem(guiGraphics, nextTerminal, getX(), getY() + 1);
        } else {
            Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER.getBlitter().dest(getX() - 1, getY() + 1, 18, 20)
                    .blit(guiGraphics);
            renderScaledItem(guiGraphics, nextTerminal, getX(), getY() + 2);
        }

        guiGraphics.pose().popPose();
    }

    private void renderScaledItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        guiGraphics.pose().pushPose();

        var mc = Minecraft.getInstance();

        BakedModel bakedmodel = mc.getItemRenderer().getModel(stack, null, null, 0);
        guiGraphics.pose().translate((float) (x + 8), (float) (y + 8), (float) (250));
        guiGraphics.pose().scale(16.0F, -16.0F, 16.0F);
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }
        mc.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, guiGraphics.pose(),
                guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        guiGraphics.flush();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        guiGraphics.pose().popPose();
    }
}
