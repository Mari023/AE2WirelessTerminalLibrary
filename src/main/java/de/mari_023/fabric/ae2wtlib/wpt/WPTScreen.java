package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.items.ItemTerminalScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.TabButton;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantic;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.io.IOException;

public class WPTScreen extends ItemTerminalScreen<WPTContainer> implements IUniversalTerminalCapable {

    private final TabButton tabCraftButton;
    private final TabButton tabProcessButton;
    private final ActionButton substitutionsEnabledBtn;
    private final ActionButton substitutionsDisabledBtn;
    private final ActionButton convertItemsToFluidsBtn;

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

        tabCraftButton = new TabButton(new ItemStack(Blocks.CRAFTING_TABLE), GuiText.CraftingPattern.text(), itemRenderer, btn -> getScreenHandler().setCraftingMode(false));
        widgets.add("craftingPatternMode", tabCraftButton);

        tabProcessButton = new TabButton(new ItemStack(Blocks.FURNACE), GuiText.ProcessingPattern.text(), itemRenderer, btn -> getScreenHandler().setCraftingMode(true));
        widgets.add("processingPatternMode", tabProcessButton);

        substitutionsEnabledBtn = new ActionButton(ActionItems.ENABLE_SUBSTITUTION, act -> getScreenHandler().setSubstitute(false));
        substitutionsEnabledBtn.setHalfSize(true);
        widgets.add("substitutionsEnabled", substitutionsEnabledBtn);

        substitutionsDisabledBtn = new ActionButton(ActionItems.DISABLE_SUBSTITUTION, act -> getScreenHandler().setSubstitute(true));
        substitutionsDisabledBtn.setHalfSize(true);
        widgets.add("substitutionsDisabled", substitutionsDisabledBtn);

        convertItemsToFluidsBtn = new ActionButton(ActionItems.FIND_CONTAINED_FLUID, act -> getScreenHandler().convertItemsToFluids());
        convertItemsToFluidsBtn.setHalfSize(true);
        widgets.add("convertItemsToFluids", convertItemsToFluidsBtn);

        ActionButton clearBtn = addDrawable(new ActionButton(ActionItems.CLOSE, btn -> getScreenHandler().clear()));
        clearBtn.setHalfSize(true);
        widgets.add("clearPattern", clearBtn);
        widgets.add("encodePattern", new ActionButton(ActionItems.ENCODE, act -> getScreenHandler().encode()));

        if(getScreenHandler().isWUT()) widgets.add("cycleTerminal", new CycleTerminalButton(btn -> cycleTerminal()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        if(handler.isCraftingMode()) {
            tabCraftButton.visible = true;
            tabProcessButton.visible = false;
            if(handler.isSubstitution()) {
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

        setSlotsHidden(SlotSemantic.CRAFTING_RESULT, !handler.isCraftingMode());
        setSlotsHidden(SlotSemantic.PROCESSING_PRIMARY_RESULT, handler.isCraftingMode());
        setSlotsHidden(SlotSemantic.PROCESSING_FIRST_OPTIONAL_RESULT, handler.isCraftingMode());
        setSlotsHidden(SlotSemantic.PROCESSING_SECOND_OPTIONAL_RESULT, handler.isCraftingMode());
        convertItemsToFluidsBtn.visible = getScreenHandler().canConvertItemsToFluids();
    }

    public void drawBG(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        if(handler.isCraftingMode()) return;
        Blitter.texture("guis/pattern_modes.png").src(97, 72, 24, 64).dest(x + 106, y + backgroundHeight - 164).blit(matrixStack, getZOffset());
    }
}