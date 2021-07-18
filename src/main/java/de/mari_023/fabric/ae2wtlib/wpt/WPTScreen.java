package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.items.ItemTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.TabButton;
import appeng.core.localization.GuiText;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class WPTScreen extends ItemTerminalScreen<WPTContainer> implements IUniversalTerminalCapable {

    private static final byte SUBSITUTION_DISABLE = 0;
    private static final byte SUBSITUTION_ENABLE = 1;

    private static final byte CRAFTMODE_CRAFTING = 1;
    private static final byte CRAFTMODE_PROCESSING = 0;

    private final TabButton tabCraftButton;
    private final TabButton tabProcessButton;
    private final ActionButton substitutionsEnabledBtn;
    private final ActionButton substitutionsDisabledBtn;

    private static final ScreenStyle STYLE;

    static {
        ScreenStyle STYLE1;
        try {
            STYLE1 = StyleManager.loadStyleDoc("/screens/wtlib/wireless_pattern_terminal.json");
        } catch(IOException ignored) {
            STYLE1 = null;
        }
        STYLE = STYLE1;
    }

    public WPTScreen(WPTContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title, STYLE);

        tabCraftButton = new TabButton(new ItemStack(Blocks.CRAFTING_TABLE), GuiText.CraftingPattern.text(), itemRenderer, btn -> toggleCraftMode(CRAFTMODE_PROCESSING));
        widgets.add("craftingPatternMode", tabCraftButton);

        tabProcessButton = new TabButton(new ItemStack(Blocks.FURNACE), GuiText.ProcessingPattern.text(), itemRenderer, btn -> toggleCraftMode(CRAFTMODE_CRAFTING));
        widgets.add("processingPatternMode", tabProcessButton);

        substitutionsEnabledBtn = new ActionButton(ActionItems.ENABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_DISABLE));
        substitutionsEnabledBtn.setHalfSize(true);
        widgets.add("substitutionsEnabled", substitutionsEnabledBtn);

        substitutionsDisabledBtn = new ActionButton(ActionItems.DISABLE_SUBSTITUTION, act -> toggleSubstitutions(SUBSITUTION_ENABLE));
        substitutionsDisabledBtn.setHalfSize(true);
        widgets.add("substitutionsDisabled", substitutionsDisabledBtn);

        ActionButton clearBtn = addButton(new ActionButton(ActionItems.CLOSE, btn -> clear()));
        clearBtn.setHalfSize(true);
        widgets.add("clearPattern", clearBtn);
        widgets.add("encodePattern", new ActionButton(ActionItems.ENCODE, act -> encode()));

        if(getScreenHandler().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));
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
        }
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
    }
}