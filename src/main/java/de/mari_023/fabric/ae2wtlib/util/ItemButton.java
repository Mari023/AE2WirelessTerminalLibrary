package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.widgets.ITooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ItemButton extends ButtonWidget implements ITooltip {

    private final Identifier texture;
    public static final Identifier TEXTURE_STATES = new Identifier("appliedenergistics2", "textures/guis/states.png");

    public ItemButton(PressAction onPress, Identifier texture) {
        super(0, 0, 16, 16, LiteralText.EMPTY, onPress);
        this.texture = texture;
    }

    public void setVisibility(final boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        super.playDownSound(soundManager);
    }

    @Override
    public void renderButton(MatrixStack matrices, final int mouseX, final int mouseY, float partial) {

        MinecraftClient minecraft = MinecraftClient.getInstance();

        if(!visible) return;
        matrices.push();
        TextureManager textureManager = minecraft.getTextureManager();
        textureManager.bindTexture(TEXTURE_STATES);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        drawTexture(matrices, x, y, 256 - 16, 256 - 16, 16, 16);
        if(active) RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        else RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        textureManager.bindTexture(texture);
        drawTexture(matrices, x, y, 0, 0, 256, 256, 256, height, width, 256);
        matrices.pop();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if(isHovered()) renderTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public @NotNull List<Text> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public int getTooltipAreaX() {
        return x;
    }

    @Override
    public int getTooltipAreaY() {
        return y;
    }

    @Override
    public int getTooltipAreaWidth() {
        return width;
    }

    @Override
    public int getTooltipAreaHeight() {
        return height;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    public void setHalfSize(final boolean halfSize) {
        if(halfSize) {
            width = 16;
            height = 16;
        } else {
            width = 8;
            height = 8;
        }
    }
}