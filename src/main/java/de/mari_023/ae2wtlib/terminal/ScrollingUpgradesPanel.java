package de.mari_023.ae2wtlib.terminal;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.Upgrades;
import appeng.client.Point;
import appeng.client.gui.*;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.localization.GuiText;
import appeng.menu.slot.AppEngSlot;

public class ScrollingUpgradesPanel implements ICompositeWidget {
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 5;
    private static final int MAX_ROWS = 8;
    private static final int SCROLLBAR_WIDTH = 5;

    private final List<Slot> slots;

    // The screen origin in window space (used to layout slots)
    private Point screenOrigin = Point.ZERO;

    // Relative to current screen origin (not window)
    private int x;
    private int y;

    private int maxRows = MAX_ROWS;
    private final Scrollbar scrollbar;

    private final List<Component> tooltips;

    public ScrollingUpgradesPanel(List<Slot> slots, IUpgradeableObject upgradeableObject, WidgetContainer widgets) {
        this.slots = slots;
        tooltips = Upgrades.getTooltipLinesForMachine(upgradeableObject.getUpgrades().getUpgradableItem());
        tooltips.addFirst(GuiText.CompatibleUpgrades.text());

        scrollbar = widgets.addScrollBar("upgradeScrollbar", Scrollbar.SMALL);
        // The scrollbar ranges from 0 to the number of rows not visible
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), 1);
        scrollbar.setCaptureMouseWheel(false);
    }

    @Override
    public void setPosition(Point position) {
        x = position.getX();
        y = position.getY();
    }

    /**
     * Changes where the panel is positioned. Coordinates are relative to the current screen's origin.
     */
    @Override
    public void setSize(int width, int height) {
        maxRows = (height - PADDING * 2) / SLOT_SIZE;
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), 1);
        scrollbar.setVisible(scrolling());
    }

    /**
     * The overall bounding box in screen coordinates.
     */
    @Override
    public Rect2i getBounds() {
        int height = 2 * PADDING + getVisibleSlotCount() * SLOT_SIZE;
        int width = 2 * PADDING + SLOT_SIZE + (scrolling() ? SCROLLBAR_WIDTH : 0);
        return new Rect2i(x, y, width, height);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        this.screenOrigin = Point.fromTopLeft(bounds);
    }

    @Override
    public void updateBeforeRender() {
        int slotOriginX = x + PADDING;
        int slotOriginY = y + PADDING;
        int currentFirstSlot = scrollbar.getCurrentScroll();

        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            if (!(s instanceof AppEngSlot slot))
                continue;

            if (currentFirstSlot <= i && currentFirstSlot + maxRows > i) {
                slot.setSlotEnabled(true);
            } else {
                slot.setSlotEnabled(false);
                continue;
            }

            if (!slot.isActive()) {
                continue;
            }

            slot.x = slotOriginX + 1;
            slot.y = slotOriginY + 1;
            slotOriginY += SLOT_SIZE;
        }
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        int slotCount = getVisibleSlotCount();
        if (slotCount <= 0) {
            return;
        }

        // This is the absolute x,y coord of the first slot within the panel
        int slotOriginX = screenOrigin.getX() + x;
        int slotOriginY = screenOrigin.getY() + y + PADDING;
        UpgradeBackground bg = UpgradeBackground.get(scrolling());

        bg.top().getBlitter().dest(slotOriginX, slotOriginY - PADDING).blit(guiGraphics);
        for (int i = 1; i < slotCount - 1; i++) {
            bg.middle().getBlitter().dest(slotOriginX, slotOriginY + i * SLOT_SIZE).blit(guiGraphics);
        }
        bg.bottom().getBlitter().dest(slotOriginX, slotOriginY + (slotCount - 1) * SLOT_SIZE).blit(guiGraphics);
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        // Use a bit of a margin around the zone to avoid things looking too cramped
        exclusionZones.add(Rects.expand(Rects.move(getBounds(), screenBounds.getX(), screenBounds.getY()), 2));
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        if (getUpgradeSlotCount() == 0) {
            return null;
        }

        return new Tooltip(tooltips);
    }

    /**
     * We need this function since the cell workbench can dynamically change how many upgrade slots are active based on
     * the cell in the workbench.
     */
    private int getUpgradeSlotCount() {
        int count = 0;
        for (Slot slot : slots) {
            if (slot instanceof AppEngSlot) {
                count++;
            }
        }
        return count;
    }

    private int getVisibleSlotCount() {
        return Math.min(maxRows, getUpgradeSlotCount());
    }

    private boolean scrolling() {
        return getUpgradeSlotCount() > maxRows;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        return scrollbar.onMouseWheel(mousePos, delta);
    }
}
