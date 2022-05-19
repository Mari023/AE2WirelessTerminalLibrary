package de.mari_023.ae2wtlib.wct;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
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
    public void renderBg(PoseStack matrices, Minecraft client, int mouseX, int mouseY) {
        InventoryScreen.renderEntityInInventory(x, y, 30, x - mouseX, y - 44 - mouseY, entity);
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
    }
}
