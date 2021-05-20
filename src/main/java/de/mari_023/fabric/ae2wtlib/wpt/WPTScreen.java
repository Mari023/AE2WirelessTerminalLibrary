package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.config.ActionItems;
import appeng.client.gui.implementations.MEMonitorableScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.TabButton;
import appeng.core.localization.GuiText;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import me.shedaniel.math.Rectangle;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.List;

public class WPTScreen extends MEMonitorableScreen<WPTContainer> implements IUniversalTerminalCapable {

    private int rows = 0;
    private AETextField searchField;
    private final int reservedSpace;
    private final WPTContainer container;

    private static final byte SUBSITUTION_DISABLE = 0;
    private static final byte SUBSITUTION_ENABLE = 1;

    private static final byte CRAFTMODE_CRAFTING = 1;
    private static final byte CRAFTMODE_PROCESSING = 0;

    private TabButton tabCraftButton;
    private TabButton tabProcessButton;
    private ActionButton substitutionsEnabledBtn;
    private ActionButton substitutionsDisabledBtn;

    public WPTScreen(WPTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        this.container = container;
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
                btn -> toggleCraftMode(CRAFTMODE_CRAFTING));
        addButton(tabProcessButton);

        substitutionsEnabledBtn = new ActionButton(x + 84, y + backgroundHeight - 165, ActionItems.ENABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_DISABLE));
        substitutionsEnabledBtn.setHalfSize(true);
        addButton(substitutionsEnabledBtn);

        substitutionsDisabledBtn = new ActionButton(x + 84, y + backgroundHeight - 165, ActionItems.DISABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_ENABLE));
        substitutionsDisabledBtn.setHalfSize(true);
        addButton(substitutionsDisabledBtn);

        ActionButton clearBtn = addButton(new ActionButton(x + 74, y + backgroundHeight - 165, ActionItems.CLOSE, btn -> clear()));
        clearBtn.setHalfSize(true);

        ActionButton encodeBtn = new ActionButton(x + 147, y + backgroundHeight - 144, ActionItems.ENCODE, act -> encode());
        addButton(encodeBtn);

        if(container.isWUT()) addButton(new CycleTerminalButton(x - 18, y + 108, btn -> cycleTerminal()));

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

    private void toggleCraftMode(byte mode) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("PatternTerminal.CraftMode");
        buf.writeByte(mode);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void toggleSubstitutions(byte mode) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("PatternTerminal.Substitute");
        buf.writeByte(mode);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void encode() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("PatternTerminal.Encode");
        buf.writeByte(0);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    private void clear() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString("PatternTerminal.Clear");
        buf.writeByte(0);
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "general"), buf);
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        bindTexture(getBackground());
        final int x_width = 197;
        drawTexture(matrices, offsetX, offsetY, 0, 0, x_width, 18);

        for(int x = 0; x < rows; x++) drawTexture(matrices, offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);

        drawTexture(matrices, offsetX, offsetY + 16 + rows * 18, 0, 106 - 18 - 18, x_width, 99 + reservedSpace);

        if(handler.isCraftingMode()) {
            tabCraftButton.visible = true;
            tabProcessButton.visible = false;

            if(handler.substitute) {
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
            drawTexture(matrices, offsetX + 109, offsetY + 36 + rows * 18, 109, 108, 18, 18);
            drawTexture(matrices, offsetX + 109, offsetY + 72 + rows * 18, 109, 108, 18, 18);
        }
        searchField.render(matrices, mouseX, mouseY, partialTicks);
        bindTexture("guis/crafting.png");
        drawTexture(matrices, offsetX + 197, offsetY, 197, 0, 46, 128); //draw viewcell background
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
        textRenderer.draw(matrices, GuiText.PatternTerminal.text(), 8, backgroundHeight - 96 + 1 - reservedSpace, 4210752);
    }

    @Override
    protected String getBackground() {
        return "wtlib/gui/pattern.png";
    }

    @Override
    public List<Rectangle> getExclusionZones() {
        List<Rectangle> zones = super.getExclusionZones();
        zones.add(new Rectangle(x + 195, y, 24, backgroundHeight - 110));
        return zones;
    }
}