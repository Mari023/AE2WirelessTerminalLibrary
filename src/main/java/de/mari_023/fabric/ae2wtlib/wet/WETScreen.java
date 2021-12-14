package de.mari_023.fabric.ae2wtlib.wet;

import appeng.client.gui.me.items.PatternTermScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import com.mojang.blaze3d.vertex.PoseStack;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WETScreen extends PatternTermScreen<WETMenu> implements IUniversalTerminalCapable {

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/wtlib/wireless_pattern_encoding_terminal.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WETScreen(WETMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, STYLE);

        if(getMenu().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));
    }

    public void drawBG(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        Blitter.texture("wtlib/guis/pattern_encoding.png").src(76, 143, 24, 10).dest(leftPos + 76, topPos + imageHeight - 106).blit(matrixStack, getBlitOffset());
    }
}