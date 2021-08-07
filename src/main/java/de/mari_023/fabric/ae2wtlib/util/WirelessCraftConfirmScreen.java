package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.AbstractTableRenderer;
import appeng.client.gui.me.crafting.CraftConfirmTableRenderer;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.Scrollbar;
import appeng.container.me.crafting.CraftingPlanSummary;
import appeng.container.me.crafting.WirelessCraftConfirmContainer;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.ConfigValuePacket;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.text.NumberFormat;

public class WirelessCraftConfirmScreen extends AEBaseScreen<WirelessCraftConfirmContainer> {
    private final CraftConfirmTableRenderer table;
    private final ButtonWidget start;
    private final ButtonWidget selectCPU;
    private final Scrollbar scrollbar;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/craft_confirm.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WirelessCraftConfirmScreen(WirelessCraftConfirmContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title, STYLE);
        this.table = new CraftConfirmTableRenderer(this, 9, 19);

        scrollbar = widgets.addScrollBar("scrollbar");

        start = widgets.addButton("start", GuiText.Start.text(), this::start);
        start.active = false;

        selectCPU = widgets.addButton("selectCpu", getNextCpuButtonLabel(), this::selectNextCpu);
        selectCPU.active = false;

        widgets.addButton("cancel", GuiText.Cancel.text(), handler::goBack);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        selectCPU.setMessage(getNextCpuButtonLabel());

        CraftingPlanSummary plan = handler.getPlan();
        boolean planIsStartable = plan != null && !plan.isSimulation();
        start.active = !handler.hasNoCPU() && planIsStartable;
        selectCPU.active = planIsStartable;

        // Show additional status about the selected CPU and plan when the planning is done
        Text planDetails = GuiText.CalculatingWait.text();
        Text cpuDetails = LiteralText.EMPTY;
        if(plan != null) {
            String byteUsed = NumberFormat.getInstance().format(plan.getUsedBytes());
            planDetails = GuiText.BytesUsed.text(byteUsed);

            if(plan.isSimulation()) cpuDetails = GuiText.Simulation.text();
            else if(handler.getCpuAvailableBytes() > 0) {
                cpuDetails = GuiText.ConfirmCraftCpuStatus.text(
                        handler.getCpuAvailableBytes(),
                        handler.getCpuCoProcessors());
            } else cpuDetails = GuiText.ConfirmCraftNoCpu.text();
        }

        setTextContent(TEXT_ID_DIALOG_TITLE, GuiText.CraftingPlan.text(planDetails));
        setTextContent("cpu_status", cpuDetails);

        final int size = plan != null ? plan.getEntries().size() : 0;
        scrollbar.setRange(0, AbstractTableRenderer.getScrollableRows(size), 1);
    }

    private Text getNextCpuButtonLabel() {
        if(handler.hasNoCPU()) return GuiText.NoCraftingCPUs.text();

        Text cpuName;
        if(handler.cpuName == null) cpuName = GuiText.Automatic.text();
        else cpuName = handler.cpuName;

        return GuiText.SelectedCraftingCPU.text(cpuName);
    }

    @Override
    public void drawFG(MatrixStack matrixStack, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {

        CraftingPlanSummary plan = handler.getPlan();
        if(plan != null) table.render(matrixStack, mouseX, mouseY, plan.getEntries(), scrollbar.getCurrentScroll());
    }

    // Allow players to confirm a craft via the enter key
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
        if(!checkHotbarKeys(InputUtil.fromKeyCode(keyCode, scanCode)) && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            start();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
    }

    private void selectNextCpu() {
        final boolean backwards = isHandlingRightClick();
        NetworkHandler.instance().sendToServer(new ConfigValuePacket("Terminal.Cpu", backwards ? "Prev" : "Next"));
    }

    private void start() {
        NetworkHandler.instance().sendToServer(new ConfigValuePacket("Terminal.Start", "Start"));
    }
}