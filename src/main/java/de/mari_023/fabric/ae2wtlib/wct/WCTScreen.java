package de.mari_023.fabric.ae2wtlib.wct;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.Icon;
import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.util.ItemButton;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.fabric.ae2wtlib.wut.IUniversalTerminalCapable;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {

    ItemButton magnetCardToggleButton;

    public WCTScreen(WCTMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        IconButton deleteButton = new IconButton(btn -> getMenu().deleteTrashSlot()) {
            @Override
            protected Icon getIcon() {
                return Icon.CONDENSER_OUTPUT_TRASH;
            }
        };
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(TextConstants.DELETE);
        widgets.add("emptyTrash", deleteButton);

        if (getMenu().isWUT())
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));

        magnetCardToggleButton = new ItemButton(btn -> setMagnetMode(),
                new ResourceLocation(AE2wtlib.MOD_NAME, "textures/magnet_card.png"));
        addToLeftToolbar(magnetCardToggleButton);

        widgets.add("player", new PlayerEntityWidget(getPlayer()));
    }

    private void setMagnetMode() {
        if (isHandlingRightClick()) {
            switch (getMenu().getMagnetSettings().magnetMode) {
                case INVALID:
                case NO_CARD:
                    break;
                case OFF:
                    getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                    break;
                case PICKUP_INVENTORY:
                    getMenu().setMagnetMode(MagnetMode.OFF);
                    break;
                case PICKUP_ME:
                    getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
                    break;
            }
        } else {
            switch (getMenu().getMagnetSettings().magnetMode) {
                case INVALID:
                case NO_CARD:
                    break;
                case OFF:
                    getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
                    break;
                case PICKUP_INVENTORY:
                    getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                    break;
                case PICKUP_ME:
                    getMenu().setMagnetMode(MagnetMode.OFF);
                    break;
            }
        }
    }

    private void setMagnetModeText() {
        switch (getMenu().getMagnetSettings().magnetMode) {
            case INVALID, NO_CARD -> magnetCardToggleButton.setVisibility(false);
            case OFF -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_OFF);
            }
            case PICKUP_INVENTORY -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_INVENTORY);
            }
            case PICKUP_ME -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_ME);
            }
        }
    }

    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        try {
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception ignored) {
        }
    }
}
