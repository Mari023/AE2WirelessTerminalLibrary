package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.NumberEntryWidget;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.core.localization.GuiText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.io.IOException;

public class WirelessCraftAmountScreen extends AEBaseScreen<WirelessCraftAmountContainer> {

    private final ButtonWidget next;

    private final NumberEntryWidget amountToCraft;

    private boolean initialAmountInitialized;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/craft_amount.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WirelessCraftAmountScreen(WirelessCraftAmountContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title, STYLE);

        next = widgets.addButton("next", GuiText.Next.text(), this::confirm);

        ae2wtlibSubScreen subGui = new ae2wtlibSubScreen(container.getTarget());
        subGui.addBackButton("back", widgets);

        amountToCraft = new NumberEntryWidget(NumberEntryType.CRAFT_ITEM_COUNT);
        amountToCraft.setValue(1);
        amountToCraft.setTextFieldBounds(62, 57, 50);
        amountToCraft.setMinValue(1);
        amountToCraft.setHideValidationIcon(true);
        amountToCraft.setOnConfirm(this::confirm);
        widgets.add("amountToCraft", amountToCraft);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if(handler.getInitialAmount() != -1 && !initialAmountInitialized) {
            amountToCraft.setValue(handler.getInitialAmount());
            initialAmountInitialized = true;
        }

        next.setMessage(hasShiftDown() ? GuiText.Start.text() : GuiText.Next.text());
        next.active = amountToCraft.getIntValue().orElse(0) > 0;
    }

    private void confirm() {
        int amount = amountToCraft.getIntValue().orElse(0);
        if(amount <= 0) return;
        handler.confirm(amount, hasShiftDown());
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);

        amountToCraft.render(matrices, offsetX, offsetY, partialTicks);
    }
}