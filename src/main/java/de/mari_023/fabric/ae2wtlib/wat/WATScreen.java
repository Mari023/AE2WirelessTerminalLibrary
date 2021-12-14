package de.mari_023.fabric.ae2wtlib.wat;

import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import com.mojang.blaze3d.vertex.PoseStack;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WATScreen extends InterfaceTerminalScreen<WATMenu> implements IUniversalTerminalCapable {

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle style;
        try {
            style = StyleManager.loadStyleDoc("/screens/wtlib/wireless_pattern_access_terminal.json");
        } catch(IOException ignored) {
            style = null;
        }
        STYLE = style;
    }

    public WATScreen(WATMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, STYLE);
        if(getMenu().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));
    }

    @Override
    public void drawBG(PoseStack matrixStack, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        Blitter.texture("wtlib/guis/pattern_access.png").src(172, 128, 23, 24).dest(leftPos + 172, topPos + imageHeight - 94).blit(matrixStack, getBlitOffset());
    }
}