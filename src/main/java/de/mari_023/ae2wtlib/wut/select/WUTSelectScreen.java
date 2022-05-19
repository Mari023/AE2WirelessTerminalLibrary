package de.mari_023.ae2wtlib.wut.select;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;

import me.shedaniel.math.Color;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public class WUTSelectScreen extends Screen {

    private final List<String> terminals = new ArrayList<>();

    public WUTSelectScreen(ItemStack terminal) {
        super(Component.translatable("gui.ae2wtlib.wireless_universal_terminal"));
        for (String currentTerminal : WUTHandler.terminalNames) {
            if (WUTHandler.hasTerminal(terminal, currentTerminal))
                terminals.add(currentTerminal);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int angle = 360 / terminals.size();
        float hWidth = width / 2F;
        float hHeight = height / 2F;
        int selected = (int) AngleHelper.getAngle(mouseX - hWidth, mouseY - (hHeight)) / angle;

        for (int i = 0; i < terminals.size(); i++)
            drawSegment(poseStack, i * angle, (i + 1) * angle, hWidth, hHeight, 100, 50, Color.ofRGB(125, 125, 125),
                    i == selected);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public static void drawSegment(PoseStack poseStack, int startingAngle, int endingAngle, float centerX,
                                   float centerY, int outerRadius, int innerRadius, Color color, boolean selected) {
        if (selected) {
            outerRadius += 10;
            color = color.darker(2);
        }

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        Matrix4f modelMatrix = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);// TODO use quads and
        // segment it

        for (int i = endingAngle; i >= startingAngle; i--) {
            float x = AngleHelper.getX(i, outerRadius);
            float y = AngleHelper.getY(i, outerRadius);
            bufferBuilder.vertex(modelMatrix, centerX + x, centerY - y, 0)
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }
        for (int i = startingAngle; i <= endingAngle; i++) {
            float x = AngleHelper.getX(i, innerRadius);
            float y = AngleHelper.getY(i, innerRadius);
            bufferBuilder.vertex(modelMatrix, centerX + x, centerY - y, 0)
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }

        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
