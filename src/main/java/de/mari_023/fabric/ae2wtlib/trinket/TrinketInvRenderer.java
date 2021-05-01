package de.mari_023.fabric.ae2wtlib.trinket;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.emi.trinkets.TrinketInventoryRenderer;
import dev.emi.trinkets.api.TrinketSlots;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;

public class TrinketInvRenderer {
    public static <T extends ScreenHandler> void renderGroupFront(MatrixStack matrices, HandledScreen<T> screen, TextureManager manager, int left, int top, TrinketSlots.SlotGroup group, int groupX, int groupY) {
        int count = group.slots.size();
        if(group.onReal) count++;
        int l = count / 2;
        int r = count - l - 1;
        GlStateManager.disableDepthTest();
        manager.bindTexture(TrinketInventoryRenderer.MORE_SLOTS_TEX);
        screen.drawTexture(matrices, left + groupX, top + groupY - 4, 4, 0, 18, 26);
        screen.drawTexture(matrices, left + groupX - 18 * l - 4, top + groupY - 4, 0, 0, 4, 26);
        screen.drawTexture(matrices, left + groupX + 18 * (r + 1), top + groupY - 4, 22, 0, 4, 26);
        for (int i = 0; i < l; i++) {
            screen.drawTexture(matrices, left + groupX - 18 * (i + 1), top + groupY - 4, 4, 0, 18, 26);
        }
        for (int i = 0; i < r; i++) {
            screen.drawTexture(matrices, left + groupX + 18 * (i + 1), top + groupY - 4, 4, 0, 18, 26);
        }
        GlStateManager.enableDepthTest();
    }

    public static <T extends ScreenHandler> void renderExcessSlotGroups(MatrixStack matrices, HandledScreen<T> screen, TextureManager manager, int left, int top, int lastX, int lastY) {
        int xIndex = (lastX + 15) / -18;
        int yIndex = (lastY - 7) / 18;
        manager.bindTexture(TrinketInventoryRenderer.MORE_SLOTS_TEX);
        //Top segments
        for (int i = 0; i <= xIndex; i++) {
            screen.drawTexture(matrices, left - 15 - i * 18, top + 3, 4, 0, 18, 4);
        }
        //Top left corner
        screen.drawTexture(matrices, left - 19 - xIndex * 18, top + 3, 0, 0, 4, 4);
        //Bottom segments
        for (int i = 0; i < xIndex; i++) {
            screen.drawTexture(matrices, left - 15 - i * 18, top + 79, 4, 22, 18, 4);
        }
        //Furthest left side segments
        for (int i = 0; i <= yIndex; i++) {
            screen.drawTexture(matrices, left - 19 - xIndex * 18, top + 7 + i * 18, 0, 4, 4, 18);
        }
        //Bottom left side corner
        screen.drawTexture(matrices, left - 19 - xIndex * 18, top + 25 + yIndex * 18, 0, 22, 4, 4);
        if (xIndex != 0) {
            //Bottom layer left corner
            screen.drawTexture(matrices, left - 1 - xIndex * 18, top + 79, 0, 22, 4, 4);
            //Inner left side segments
            for (int i = yIndex; i < 3; i++) {
                screen.drawTexture(matrices, left - 1 - xIndex * 18, top + 25 + i * 18, 0, 4, 4, 18);
            }
            //Left column bottom layer
            if (yIndex == 3) {
                screen.drawTexture(matrices, left - 15 - xIndex * 18, top + 25 + yIndex * 18, 4, 22, 18, 4);
            } else {
                screen.drawTexture(matrices, left - 15 - xIndex * 18, top + 25 + yIndex * 18, 4, 22, 15, 4);
            }
        } else {
            screen.drawTexture(matrices, left - 15, top + 25 + yIndex * 18, 4, 22, 18, 4);
        }
    }
}