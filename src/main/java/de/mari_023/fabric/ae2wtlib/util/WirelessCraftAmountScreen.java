package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.NumberEntryWidget;
import appeng.core.localization.GuiText;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WirelessCraftAmountScreen extends AEBaseScreen<WirelessCraftAmountContainer> {
    private final ae2wtlibSubScreen subGui;

    private NumberEntryWidget amountToCraft;

    private ButtonWidget next;

    public WirelessCraftAmountScreen(WirelessCraftAmountContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        subGui = new ae2wtlibSubScreen(this, container.getTarget());
    }

    @Override
    public void init() {
        super.init();

        amountToCraft = new NumberEntryWidget(this, 20, 30, 138, 62, NumberEntryType.CRAFT_ITEM_COUNT);
        amountToCraft.setValue(1);
        amountToCraft.setTextFieldBounds(62, 57, 50);
        amountToCraft.setMinValue(1);
        amountToCraft.setHideValidationIcon(true);
        amountToCraft.addButtons(children::add, this::addButton);

        next = addButton(new ButtonWidget(x + 128, y + 51, 38, 20, GuiText.Next.text(), this::confirm));
        amountToCraft.setOnConfirm(() -> confirm(next));

        subGui.addBackButton(this::addButton, 154, 0);

        changeFocus(true);
    }

    private void confirm(ButtonWidget button) {
        int amount = amountToCraft.getIntValue().orElse(0);
        if(amount <= 0) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(amount);
        buf.writeBoolean(hasShiftDown());
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "craft_request"), buf);
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        textRenderer.draw(matrices, GuiText.SelectAmount.text(), 8, 6, 4210752);
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        next.setMessage(hasShiftDown() ? GuiText.Start.text() : GuiText.Next.text());

        bindTexture("guis/craft_amt.png");
        drawTexture(matrices, offsetX, offsetY, 0, 0, backgroundWidth, backgroundHeight);

        next.active = amountToCraft.getIntValue().orElse(0) > 0;

        amountToCraft.render(matrices, offsetX, offsetY, partialTicks);
    }
}