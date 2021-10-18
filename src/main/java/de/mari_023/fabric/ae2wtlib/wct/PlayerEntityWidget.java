package de.mari_023.fabric.ae2wtlib.wct;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;

public class PlayerEntityWidget extends ClickableWidget {
    private final LivingEntity entity;

    public PlayerEntityWidget(LivingEntity entity) {
        super(0, 0, 0, 0, new LiteralText(""));
        this.entity = entity;
    }

    @Override
    public void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        InventoryScreen.drawEntity(x, y, 30, x - mouseX, y - 44 - mouseY, entity);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}
}
