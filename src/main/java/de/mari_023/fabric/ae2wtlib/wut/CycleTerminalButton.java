package de.mari_023.fabric.ae2wtlib.wut;

import appeng.client.gui.widgets.ITooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CycleTerminalButton extends ButtonWidget implements ITooltip {

    public CycleTerminalButton(PressAction onPress) {
        super(0, 0, 16, 16, TextConstants.CYCLE, onPress);
        visible = true;
        active = true;
    }

    @Override
    public @NotNull
    List<Text> getTooltipMessage() {
        return Collections.singletonList(TextConstants.CYCLE_TOOLTIP);
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
        return true;
    }

    public static final Identifier TEXTURE_STATES = new Identifier("ae2", "textures/guis/states.png");
    public static final Identifier nextTerminal = new Identifier(ae2wtlib.MOD_NAME, "textures/wireless_universal_terminal.png");

    @Override
    public void renderButton(MatrixStack matrices, final int mouseX, final int mouseY, float partial) {
        if(!visible) return;
        matrices.push();
        RenderSystem.setShaderTexture(0, TEXTURE_STATES);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        drawTexture(matrices, x, y, 240, 240, 16, 16);

        RenderSystem.setShaderTexture(0, nextTerminal);

        if(active) RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        else RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);

        drawTexture(matrices, x + 1, y + 1, 14, 14, 0, 0, 512, 512, 512, 512);

        matrices.pop();
        if(isHovered()) renderTooltip(matrices, mouseX, mouseY);
    }
}