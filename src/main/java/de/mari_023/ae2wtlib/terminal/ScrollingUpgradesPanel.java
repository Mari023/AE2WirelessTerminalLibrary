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
import net.minecraft.world.item.ItemStack;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.Upgrades;
import appeng.client.Point;
import appeng.client.gui.*;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.localization.GuiText;
import appeng.menu.slot.AppEngSlot;

import de.mari_023.ae2wtlib.AE2wtlibItems;

public class ScrollingUpgradesPanel implements ICompositeWidget {
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 5;
    private static final int SCROLLBAR_WIDTH = 5;

    private final List<Slot> slots;
    private final Supplier<IUpgradeInventory> upgrades;

    // The screen origin in window space (used to layout slots)
    private Point screenOrigin = Point.ZERO;

    // Relative to current screen origin (not window)
    private int x;
    private int y;

    private int maxRows = 2;
    private final Scrollbar scrollbar;

    private final List<Component> tooltips;

    public ScrollingUpgradesPanel(List<Slot> slots, IUpgradeableObject upgradeableObject, WidgetContainer widgets,
            Supplier<IUpgradeInventory> upgrades) {
        this.slots = slots;
        this.upgrades = upgrades;
        tooltips = Upgrades.getTooltipLinesForMachine(upgradeableObject.getUpgrades().getUpgradableItem());
        tooltips.addFirst(GuiText.CompatibleUpgrades.text());

        scrollbar = widgets.addScrollBar("upgradeScrollbar", Scrollbar.SMALL);
        scrollbar.setCaptureMouseWheel(false);
        setScrollbarRange();
    }

    private boolean singularitySlotHidden() {
        return isDisabledSlotEmpty((AppEngSlot) slots.getFirst())
                && !upgrades.get().isInstalled(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
    }

    private boolean isDisabledSlotEmpty(AppEngSlot slot) {
        boolean enabled = slot.isSlotEnabled();
        slot.setSlotEnabled(true);
        ItemStack stack = slot.getItem();
        slot.setSlotEnabled(enabled);
        return stack.isEmpty();
    }

    public void setMaxRows(int rows) {
        maxRows = rows;
        setScrollbarRange();
        scrollbar.setHeight(getVisibleSlotCount() * SLOT_SIZE - 2);
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
    public void setSize(int width, int height) {}

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
        screenOrigin = Point.fromTopLeft(bounds);
    }

    @Override
    public void updateBeforeRender() {
        int slotOriginX = x;
        int slotOriginY = y + PADDING;
        int currentFirstSlot = scrollbar.getCurrentScroll();
        setScrollbarRange();

        int i = 0;
        for (Slot s : slots) {
            if (!(s instanceof AppEngSlot slot))
                continue;

            if (s == slots.getFirst() && singularitySlotHidden()) {
                ((AppEngSlot) slots.getFirst()).setSlotEnabled(false);
                continue;
            }

            slot.setSlotEnabled(currentFirstSlot <= i && currentFirstSlot + maxRows > i);

            i++;

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

    private int getUpgradeSlotCount() {
        int count = 0;
        for (Slot slot : slots) {
            if (slot instanceof AppEngSlot) {
                count++;
            }
        }
        if (singularitySlotHidden())
            count--;
        return count;
    }

    private void setScrollbarRange() {
        // The scrollbar ranges from 0 to the number of rows not visible
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), 1);
        scrollbar.setVisible(scrolling());
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
