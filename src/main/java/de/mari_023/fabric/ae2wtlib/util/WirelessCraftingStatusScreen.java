package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.me.crafting.CraftingCPUScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.container.me.crafting.WirelessCraftingStatusContainer;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.ConfigValuePacket;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.IOException;

public class WirelessCraftingStatusScreen extends CraftingCPUScreen<WirelessCraftingStatusContainer> {

    private final ButtonWidget selectCPU;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/wtlib/crafting_status.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WirelessCraftingStatusScreen(WirelessCraftingStatusContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title, STYLE);
        selectCPU = widgets.addButton("selectCpu", getNextCpuButtonLabel(), this::selectNextCpu);

        ae2wtlibSubScreen subGui = new ae2wtlibSubScreen(container.getTarget());
        subGui.addBackButton("back", widgets);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        selectCPU.setMessage(getNextCpuButtonLabel());
    }

    private Text getNextCpuButtonLabel() {
        if(handler.noCPU) return GuiText.NoCraftingJobs.text();
        // it's possible that the cpu name has not synchronized from server->client yet, since fields are synced
        // individually.
        Text name = handler.cpuName;
        if(name == null) name = LiteralText.EMPTY;
        return GuiText.SelectedCraftingCPU.text(name);
    }

    @Override
    protected Text getGuiDisplayName(final Text in) {
        return in; // the cpu name is on the button
    }

    private void selectNextCpu() {
        final boolean backwards = isHandlingRightClick();
        NetworkHandler.instance().sendToServer(new ConfigValuePacket("Terminal.Cpu", backwards ? "Prev" : "Next"));
    }
}