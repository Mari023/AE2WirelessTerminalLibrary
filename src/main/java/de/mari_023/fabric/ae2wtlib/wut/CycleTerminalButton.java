package de.mari_023.fabric.ae2wtlib.wut;

import appeng.client.gui.widgets.ITooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CycleTerminalButton extends ButtonWidget implements ITooltip {

    public CycleTerminalButton(int x, int y, PressAction onPress) {
        super(x, y, 16, 16, new TranslatableText("gui.ae2wtlib.cycle_terminal"), onPress);
        visible = true;
        active = true;
    }

    @Override
    public Text getTooltipMessage() {
        return new TranslatableText("gui.ae2wtlib.cycle_terminal.desc");
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
        return 16;
    }

    @Override
    public int getTooltipAreaHeight() {
        return 16;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return true;//TODO
    }

    public static final Identifier TEXTURE_STATES = new Identifier("appliedenergistics2", "textures/guis/states.png");

    @Override
    public void renderButton(MatrixStack matrices, final int mouseX, final int mouseY, float partial) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if(this.visible) {
            final int iconIndex = 6;

            TextureManager textureManager = minecraft.getTextureManager();
            textureManager.bindTexture(TEXTURE_STATES);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();

            if(this.active) RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            else RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);

            final int uv_y = iconIndex / 16;
            final int uv_x = iconIndex - uv_y * 16;

            drawTexture(matrices, this.x, this.y, 256 - 16, 256 - 16, 16, 16);
            drawTexture(matrices, this.x, this.y, uv_x * 16, uv_y * 16, 16, 16);

            RenderSystem.enableDepthTest();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

            if(isHovered()) renderToolTip(matrices, mouseX, mouseY);
        }
    }
}