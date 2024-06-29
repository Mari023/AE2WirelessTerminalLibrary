package de.mari_023.ae2wtlib.terminal;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    private final Supplier<List<Component>> tooltipSupplier;

    public ScrollingUpgradesPanel(List<Slot> slots, IUpgradeableObject upgradeableObject, WidgetContainer widgets) {
        this(slots, () -> Upgrades.getTooltipLinesForMachine(upgradeableObject.getUpgrades().getUpgradableItem()),
                widgets);
    }

    public ScrollingUpgradesPanel(List<Slot> slots, Supplier<List<Component>> tooltipSupplier,
            WidgetContainer widgets) {
        this.slots = slots;
        this.tooltipSupplier = tooltipSupplier;

        scrollbar = widgets.addScrollBar("upgradeScrollbar", Scrollbar.SMALL);
        // The scrollbar ranges from 0 to the number of rows not visible
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), getVisibleSlotCount());
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
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), getVisibleSlotCount());
        scrollbar.setVisible(scrolling());
    }

    /**
     * The overall bounding box in screen coordinates.
     */
    @Override
    public Rect2i getBounds() {
        int slotCount = getUpgradeSlotCount();

        int height = 2 * PADDING + Math.min(MAX_ROWS, slotCount) * SLOT_SIZE;
        int width = 2 * PADDING + SLOT_SIZE + (scrolling() ? SCROLLBAR_WIDTH : 0);
        return new Rect2i(x, y, width, height);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        this.screenOrigin = Point.fromTopLeft(bounds);
    }

    @Override
    public void updateBeforeRender() {
        int slotOriginX = x;
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
        boolean scrolling = scrolling();

        for (int i = 0; i < slotCount; i++) {
            drawSlot(guiGraphics, slotOriginX, slotOriginY + i * SLOT_SIZE, i == 0, i == slotCount - 1, scrolling);
        }
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        int offsetX = screenBounds.getX();
        int offsetY = screenBounds.getY();

        int slotCount = getVisibleSlotCount();

        // Use a bit of a margin around the zone to avoid things looking too cramped
        final int margin = 2;

        // Add a single bounding rectangle for as many columns as are fully populated
        int rightEdge = offsetX + x;
        if (slotCount > 0) {
            exclusionZones.add(Rects.expand(new Rect2i(
                    rightEdge,
                    offsetY + y,
                    PADDING * 2 + SLOT_SIZE + (scrolling() ? SCROLLBAR_WIDTH : 0),
                    PADDING * 2 + slotCount * SLOT_SIZE), margin));
        }
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        if (getUpgradeSlotCount() == 0) {
            return null;
        }

        List<Component> tooltip = this.tooltipSupplier.get();
        if (tooltip.isEmpty()) {
            return null;
        }

        return new Tooltip(tooltip);
    }

    private void drawSlot(GuiGraphics guiGraphics, int x, int y, boolean borderTop, boolean borderBottom,
            boolean scrolling) {
        if (borderTop) {
            y -= PADDING;
            if (scrolling) {
                Icon.UPGRADE_BACKGROUND_SCROLLING_TOP.getBlitter().dest(x, y).blit(guiGraphics);
            } else {
                Icon.UPGRADE_BACKGROUND_TOP.getBlitter().dest(x, y).blit(guiGraphics);
            }
        } else if (borderBottom) {
            if (scrolling) {
                Icon.UPGRADE_BACKGROUND_SCROLLING_BOTTOM.getBlitter().dest(x, y).blit(guiGraphics);
            } else {
                Icon.UPGRADE_BACKGROUND_BOTTOM.getBlitter().dest(x, y).blit(guiGraphics);
            }
        } else {
            if (scrolling) {
                Icon.UPGRADE_BACKGROUND_SCROLLING_MIDDLE.getBlitter().dest(x, y).blit(guiGraphics);
            } else {
                Icon.UPGRADE_BACKGROUND_MIDDLE.getBlitter().dest(x, y).blit(guiGraphics);
            }
        }
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
