package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.Item;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

public class ItemButton extends IconButton {
    private final Item item;

    public ItemButton(OnPress onPress, Item item) {
        super(onPress);
        this.item = item;
    }

    @Nullable
    @Override
    protected Icon getIcon() {
        return null;
    }

    @Override
    protected Item getItemOverlay() {
        return item;
    }
}
