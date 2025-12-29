package de.mari_023.ae2wtlib.wct;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;

import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.gui.IconButton;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {
    private final IconButton magnetCardMenuButton;
    private final ScrollingUpgradesPanel upgradesPanel;

    public WCTScreen(WCTMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(cycleTerminalButton());

        IconButton wirelessTerminalSettingsButton = new IconButton(
                _ -> switchToScreen(new WirelessTerminalSettingsScreen(this)), Icon.TERMINAL_SETTINGS);
        widgets.add("wirelessTerminalSettingsButton", wirelessTerminalSettingsButton);
        wirelessTerminalSettingsButton.setMessage(TextConstants.TERMINAL_SETTINGS);

        magnetCardMenuButton = new IconButton(_ -> getMenu().openMagnetMenu(), Icon.MAGNET);
        widgets.add("magnetCardMenuButton", magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        IconButton trashButton = new IconButton(_ -> getMenu().openTrashMenu(), Icon.TRASH);
        widgets.add("trashButton", trashButton);
        trashButton.setMessage(TextConstants.TRASH);

        widgets.add("player", new PlayerEntityWidget(Objects.requireNonNull(Minecraft.getInstance().player)));
        upgradesPanel = addUpgradePanel(widgets, getMenu());
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    private void setMagnetModeText() {
        magnetCardMenuButton.setVisibility(
                switch (getMenu().getMagnetMode()) {
                    case INVALID, NO_CARD -> false;
                    case OFF, PICKUP_ME, PICKUP_INVENTORY, PICKUP_ME_NO_MAGNET -> true;
                });
    }

    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean value = super.keyPressed(event);
        if (!value)
            return checkForTerminalKeys(event);
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
    public void renderSlot(GuiGraphics guiGraphics, Slot s, int mouseX, int mouseY) {
        if (s instanceof ArmorSlot armorSlot) {
            renderArmorSlot(guiGraphics, armorSlot, mouseX, mouseY);
        } else {
            super.renderSlot(guiGraphics, s, mouseX, mouseY);
        }
    }

    private void renderArmorSlot(GuiGraphics guiGraphics, ArmorSlot s, int mouseX, int mouseY) {
        var is = s.getItem();

        if (is.isEmpty() && s.isSlotEnabled()) {
            s.icon().getBlitter()
                    .dest(s.x, s.y)
                    .opacity(s.getOpacityOfIcon())
                    .blit(guiGraphics);
        }

        super.renderSlot(guiGraphics, s, mouseX, mouseY);
    }
}
