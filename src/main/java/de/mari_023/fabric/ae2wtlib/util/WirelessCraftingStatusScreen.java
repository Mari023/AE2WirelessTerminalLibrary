package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.implementations.CraftingCPUScreen;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.ConfigValuePacket;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class WirelessCraftingStatusScreen extends CraftingCPUScreen<WirelessCraftingStatusContainer> {

    private final ae2wtlibSubScreen subGui;
    private ButtonWidget selectCPU;

    public WirelessCraftingStatusScreen(WirelessCraftingStatusContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        subGui = new ae2wtlibSubScreen(this, container.getTarget());
    }

    @Override
    public void init() {
        super.init();

        selectCPU = new ButtonWidget(x + 8, y + backgroundHeight - 25, 150, 20, getNextCpuButtonLabel(), btn -> selectNextCpu());
        addButton(selectCPU);

        subGui.addBackButton(btn -> {
            addButton(btn);
            btn.setHideEdge(true);
        }, 213, -4);
    }

    @Override
    public void render(MatrixStack matrices, final int mouseX, final int mouseY, final float btn) {
        updateCPUButtonText();
        super.render(matrices, mouseX, mouseY, btn);
    }

    private void updateCPUButtonText() {
        selectCPU.setMessage(getNextCpuButtonLabel());
    }

    private Text getNextCpuButtonLabel() {
        if(handler.noCPU) return GuiText.NoCraftingJobs.text();
        return GuiText.CraftingCPU.withSuffix(": ").append(handler.cpuName);
    }

    @Override
    protected Text getGuiDisplayName(final Text in) {
        return in;
    }

    private void selectNextCpu() {
        final boolean backwards = isHandlingRightClick();
        NetworkHandler.instance().sendToServer(new ConfigValuePacket("Terminal.Cpu", backwards ? "Prev" : "Next"));
    }
}