package de.mari_023.ae2wtlib.wct;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.util.ItemButton;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

import appeng.client.gui.Icon;
import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {

    private final ItemButton magnetCardToggleButton;
    private final ItemButton magnetCardMenuButton;

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

        magnetCardMenuButton = new ItemButton(btn -> getMenu().openMagnetMenu(),
                new ResourceLocation(AE2wtlib.MOD_NAME, "textures/magnet_card.png"));
        addToLeftToolbar(magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        widgets.add("player", new PlayerEntityWidget(Objects.requireNonNull(Minecraft.getInstance().player)));
    }

    private void setMagnetMode() {
        if (isHandlingRightClick()) {
            switch (getMenu().getMagnetSettings().magnetMode) {
                case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.OFF);
                case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
            }
            return;
        }
        switch (getMenu().getMagnetSettings().magnetMode) {
            case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
            case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
            case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.OFF);
        }
    }

    private void setMagnetModeText() {
        switch (getMenu().getMagnetSettings().magnetMode) {
            case INVALID, NO_CARD -> {
                magnetCardToggleButton.setVisibility(false);
                magnetCardMenuButton.setVisibility(false);
            }
            case OFF -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_OFF);
            }
            case PICKUP_INVENTORY -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_INVENTORY);
            }
            case PICKUP_ME -> {
                magnetCardToggleButton.setVisibility(true);
                magnetCardMenuButton.setVisibility(true);
                magnetCardToggleButton.setMessage(TextConstants.MAGNETCARD_ME);
            }
        }
    }

    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        try {// TODO why do we need this?
            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception ignored) {
        }
    }
}
