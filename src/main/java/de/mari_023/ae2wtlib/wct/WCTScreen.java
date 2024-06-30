package de.mari_023.ae2wtlib.wct;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.*;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {
    private final IconButton magnetCardToggleButton;
    private final IconButton magnetCardMenuButton;

    public WCTScreen(WCTMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(cycleTerminalButton());

        magnetCardToggleButton = new IconButton(btn -> setMagnetMode(), Icon.MAGNET);
        widgets.add("magnetCardToggleButton", magnetCardToggleButton);

        magnetCardMenuButton = new IconButton(btn -> getMenu().openMagnetMenu(), Icon.MAGNET_FILTER);
        widgets.add("magnetCardMenuButton", magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        IconButton trashButton = new IconButton(btn -> getMenu().openTrashMenu(), Icon.TRASH);
        widgets.add("trashButton", trashButton);
        trashButton.setMessage(TextConstants.TRASH);

        widgets.add("player", new PlayerEntityWidget(Objects.requireNonNull(Minecraft.getInstance().player)));
        addSingularityPanel(widgets, getMenu());

        widgets.add("scrollingUpgrades",
                new ScrollingUpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE), menu.getHost(), widgets));
    }

    private void setMagnetMode() {
        if (isHandlingRightClick()) {
            switch (getMenu().getMagnetMode()) {
                case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
                case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.OFF);
                case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
            }
            return;
        }
        switch (getMenu().getMagnetMode()) {
            case OFF -> getMenu().setMagnetMode(MagnetMode.PICKUP_INVENTORY);
            case PICKUP_INVENTORY -> getMenu().setMagnetMode(MagnetMode.PICKUP_ME);
            case PICKUP_ME -> getMenu().setMagnetMode(MagnetMode.OFF);
        }
    }

    private void setMagnetModeText() {
        switch (getMenu().getMagnetMode()) {
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int keyPressed) {
        boolean value = super.keyPressed(keyCode, scanCode, keyPressed);
        if (!value)
            return checkForTerminalKeys(keyCode, scanCode);
        return true;
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }

    /**
     * This overrides the base-class method through some access transformer hackery...
     */
    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot s) {
        if (s instanceof ArmorSlot armorSlot) {
            renderArmorSlot(guiGraphics, armorSlot);
        } else {
            super.renderSlot(guiGraphics, s);
        }
    }

    private void renderArmorSlot(GuiGraphics guiGraphics, ArmorSlot s) {
        var is = s.getItem();

        if (is.isEmpty() && s.isSlotEnabled()) {
            s.icon().getBlitter()
                    .dest(s.x, s.y)
                    .opacity(s.getOpacityOfIcon())
                    .blit(guiGraphics);
        }

        super.renderSlot(guiGraphics, s);
    }
}
