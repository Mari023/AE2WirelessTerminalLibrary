package de.mari_023.fabric.ae2wtlib.wut;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WUTSelectScreen extends Screen {

    private final List<String> terminals = new ArrayList<>();

    protected WUTSelectScreen(ItemStack terminal) {
        super(new TranslatableComponent("gui.ae2wtlib.wireless_universal_terminal"));
        for(String currentTerminal : WUTHandler.terminalNames) {
            if(WUTHandler.hasTerminal(terminal, currentTerminal)) terminals.add(currentTerminal);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int angle = 360 / terminals.size();

        for(int i = 0; i < terminals.size(); i++) {
            drawDoughnutSegment(poseStack, i * angle, (i + 1) * angle, width / 2f, height / 2f, 100, 50, Color.DARK_GRAY.getRGB());
        }
    }

    public static void drawDoughnutSegment(PoseStack poseStack, int startingAngle, int endingAngle, float centerX, float centerY, double outerRingRadius, double innerRingRadius, int color) {
        float f = (float) (color >> 24 & 0xff) / 255F;
        float f1 = (float) (color >> 16 & 0xff) / 255F;
        float f2 = (float) (color >> 8 & 0xff) / 255F;
        float f3 = (float) (color & 0xff) / 255F;
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        Matrix4f modelMatrix = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        for(int i = startingAngle; i <= endingAngle; i++) {
            double x = Math.sin(Math.toRadians(i)) * innerRingRadius;
            double y = Math.cos(Math.toRadians(i)) * innerRingRadius;
            bufferBuilder.vertex(modelMatrix, (float) (centerX + x), (float) (centerY - y), 0).color(f1, f2, f3, f).endVertex();
        }
        for(int i = endingAngle; i >= startingAngle; i--) {
            double x = Math.sin(Math.toRadians(i)) * outerRingRadius;
            double y = Math.cos(Math.toRadians(i)) * outerRingRadius;
            bufferBuilder.vertex(modelMatrix, (float) (centerX + x), (float) (centerY - y), 0).color(f1, f2, f3, f).endVertex();
        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
