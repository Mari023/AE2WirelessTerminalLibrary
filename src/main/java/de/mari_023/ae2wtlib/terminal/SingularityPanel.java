package de.mari_023.ae2wtlib.terminal;

import java.util.function.Supplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.menu.slot.AppEngSlot;

import de.mari_023.ae2wtlib.AE2wtlibItems;

public class SingularityPanel implements ICompositeWidget {
    private final AppEngSlot singularity;
    private final Supplier<IUpgradeInventory> upgrades;

    // Relative to current screen origin (not window)
    private int x;
    private int y;

    public SingularityPanel(AppEngSlot singularity, Supplier<IUpgradeInventory> upgrades) {
        this.singularity = singularity;
        this.upgrades = upgrades;
    }

    @Override
    public boolean isVisible() {
        boolean visible = !singularity.getItem().isEmpty()
                || upgrades.get().isInstalled(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
        singularity.setActive(visible);
        return visible;
    }

    @Override
    public void setPosition(Point position) {
        x = position.getX();
        y = position.getY();
        singularity.x = x + 1;
        singularity.y = y + 6;
    }

    @Override
    public void setSize(int width, int height) {}

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x, y, Icon.SINGULARITY_BACKGROUND.width(), Icon.SINGULARITY_BACKGROUND.height());
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        Icon.SINGULARITY_BACKGROUND.getBlitter().dest(bounds.getX() + x, bounds.getY() + y).blit(guiGraphics);
    }
}
