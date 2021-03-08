package de.mari_023.fabric.ae2wtlib;

import appeng.api.config.ActionItems;
import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.IconButton;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class WUTScreen extends MEMonitorableScreen<WUTContainer> {

    private int rows = 0;
    private AETextField searchField;
    private final int reservedSpace;

    public WUTScreen(WUTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        reservedSpace = 73;

        try {
            Field f = MEMonitorableScreen.class.getDeclaredField("reservedSpace");
            f.setAccessible(true);
            f.set(this, reservedSpace);
            f.setAccessible(false);
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}
    }

    @Override
    public void init() {
        super.init();
        ActionButton clearBtn = addButton(new ActionButton(x + 92+43, y + backgroundHeight - 156-4, ActionItems.STASH, btn -> clear()));
        clearBtn.setHalfSize(true);

        IconButton deleteButton = addButton(new IconButton(x + 92 + 25, y + backgroundHeight - 156 +52, btn -> delete()) {
            @Override
            protected int getIconIndex() {
                return 6;
            }
        });
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(new LiteralText("Empty Trash\n" +
                "Delete contents of the Trash slot"));

        try {
            Field field = MEMonitorableScreen.class.getDeclaredField("rows");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);
            rows = (int) value;
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}
        try {
            Field field = MEMonitorableScreen.class.getDeclaredField("searchField");
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(false);
            searchField = (AETextField) value;
        } catch(IllegalAccessException | NoSuchFieldException ignored) {}
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {

        bindTexture(getBackground());
        final int x_width = 197;
        drawTexture(matrices, offsetX, offsetY, 0, 0, x_width, 18);

        for(int x = 0; x < this.rows; x++) {
            drawTexture(matrices, offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
        }

        drawTexture(matrices, offsetX, offsetY + 16 + rows * 18, 0, 106 - 18 - 18, x_width, 99 + reservedSpace);

        searchField.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
        textRenderer.draw(matrices, GuiText.CraftingTerminal.text(), 8, backgroundHeight - 96 + 1 - reservedSpace, 4210752);
    }


    private void clear() {
        Slot s = null;
        for (final Object j : handler.slots) {
            if (j instanceof CraftingMatrixSlot) {
                s = (Slot) j;
            }
        }

        if (s != null) {
            final InventoryActionPacket p = new InventoryActionPacket(InventoryAction.MOVE_REGION, s.id, 0);
            NetworkHandler.instance().sendToServer(p);
        }
    }

    private void delete() {

    }

    @Override
    protected String getBackground() {
        return "wtlib/gui/crafting.png";
    }
}