package de.mari_023.ae2wtlib.wct;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class PlayerEntityWidget extends AbstractWidget {
    private final LivingEntity entity;

    public PlayerEntityWidget(LivingEntity entity) {
        super(0, 0, 0, 0, Component.empty());
        this.entity = entity;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, getX(), getY(), getX() + 75 - 26 - 3,
                getY() + 78 - 8, 30, 0.0625F,
                mouseX,
                mouseY, entity);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
