package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.tiles.IViewCellStorage;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WCTGuiObject extends WTGuiObject implements IPortableCell, IViewCellStorage {

    private static final ItemStack ICON = new ItemStack(ae2wtlib.CRAFTING_TERMINAL);

    public WCTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot, WCTContainer.TYPE);
    }

    @Override
    public ItemStack getIcon() {
        return ICON;
    }
}