package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.config.ActionItems;
import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.TabButton;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class WPTScreen extends MEMonitorableScreen<WPTContainer> {

    private int rows = 0;
    private AETextField searchField;
    private final int reservedSpace;

    private static final boolean SUBSITUTION_DISABLE = false;
    private static final boolean SUBSITUTION_ENABLE = true;

    private static final boolean CRAFTMODE_CRFTING = true;
    private static final boolean CRAFTMODE_PROCESSING = false;

    private TabButton tabCraftButton;
    private TabButton tabProcessButton;
    private ActionButton substitutionsEnabledBtn;
    private ActionButton substitutionsDisabledBtn;

    public WPTScreen(WPTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        reservedSpace = 81;

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

        tabCraftButton = new TabButton(x + 173, y + backgroundHeight - 177,
                new ItemStack(Blocks.CRAFTING_TABLE), GuiText.CraftingPattern.text(), itemRenderer,
                btn -> toggleCraftMode(CRAFTMODE_PROCESSING));
        addButton(tabCraftButton);

        tabProcessButton = new TabButton(x + 173, y + backgroundHeight - 177,
                new ItemStack(Blocks.FURNACE), GuiText.ProcessingPattern.text(), itemRenderer,
                btn -> toggleCraftMode(CRAFTMODE_CRFTING));
        addButton(tabProcessButton);

        substitutionsEnabledBtn = new ActionButton(x + 84, y + backgroundHeight - 165,
                ActionItems.ENABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_DISABLE));
        substitutionsEnabledBtn.setHalfSize(true);
        addButton(substitutionsEnabledBtn);

        substitutionsDisabledBtn = new ActionButton(x + 84, y + backgroundHeight - 165,
                ActionItems.DISABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_ENABLE));
        substitutionsDisabledBtn.setHalfSize(true);
        addButton(substitutionsDisabledBtn);

        ActionButton clearBtn = addButton(new ActionButton(x + 74, y + backgroundHeight - 165, ActionItems.CLOSE, btn -> clear()));
        clearBtn.setHalfSize(true);

        ActionButton encodeBtn = new ActionButton(x + 147, y + backgroundHeight - 144,
                ActionItems.ENCODE, act -> encode());
        addButton(encodeBtn);

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

    private void toggleCraftMode(boolean mode) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCharSequence("PatternTerminal.CraftMode", StandardCharsets.US_ASCII);
        //buf.writeBoolean(mode);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void toggleSubstitutions(boolean mode) {
        //"PatternTerminal.Substitute", mode
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCharSequence("PatternTerminal.Substitute", StandardCharsets.US_ASCII);
        //buf.writeBoolean(mode);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void encode() {
        //"PatternTerminal.Encode", "1"
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCharSequence("PatternTerminal.Encode", StandardCharsets.US_ASCII);
        //buf.writeBoolean(false);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {

        bindTexture(getBackground());
        final int x_width = 197;
        drawTexture(matrices, offsetX, offsetY, 0, 0, x_width, 18);

        for(int x = 0; x < rows; x++) {
            drawTexture(matrices, offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
        }

        drawTexture(matrices, offsetX, offsetY + 16 + rows * 18, 0, 106 - 18 - 18, x_width, 99 + reservedSpace);

        searchField.render(matrices, mouseX, mouseY, partialTicks);

        if (handler.isCraftingMode()) {
            tabCraftButton.visible = true;
            tabProcessButton.visible = false;

            if (handler.substitute) {
                substitutionsEnabledBtn.visible = true;
                substitutionsDisabledBtn.visible = false;
            } else {
                substitutionsEnabledBtn.visible = false;
                substitutionsDisabledBtn.visible = true;
            }
        } else {
            tabCraftButton.visible = false;
            tabProcessButton.visible = true;
            substitutionsEnabledBtn.visible = false;
            substitutionsDisabledBtn.visible = false;
        }
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
        textRenderer.draw(matrices, GuiText.CraftingTerminal.text(), 8, backgroundHeight - 96 + 1 - reservedSpace, 4210752);
    }

    private void clear() {
        Slot s = null;
        for(final Object j : handler.slots) {
            if(j instanceof CraftingMatrixSlot) {
                s = (Slot) j;
            }
        }

        if(s != null) {
            final InventoryActionPacket p = new InventoryActionPacket(InventoryAction.MOVE_REGION, s.id, 0);
            NetworkHandler.instance().sendToServer(p);
        }
    }

    @Override
    protected String getBackground() {
        return "wtlib/gui/pattern.png";
    }
}