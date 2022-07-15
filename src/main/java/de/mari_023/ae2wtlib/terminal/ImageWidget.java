package de.mari_023.ae2wtlib.terminal;

import appeng.client.gui.style.Blitter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class ImageWidget extends AbstractWidget {
    private final Blitter blitter;
    private final Rect2i sourceRect;

    public ImageWidget(Blitter blitter) {
        super(0, 0, blitter.getSrcWidth(), blitter.getSrcHeight(), Component.empty());
        this.blitter = blitter.copy();
        sourceRect = new Rect2i(blitter.getSrcX(), blitter.getSrcY(), blitter.getSrcWidth(), blitter.getSrcHeight());
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
            blitter.src(sourceRect.getX(), sourceRect.getY(), sourceRect.getWidth(), sourceRect.getHeight()).dest(x, y).blit(poseStack, getBlitOffset());
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
    }
}
