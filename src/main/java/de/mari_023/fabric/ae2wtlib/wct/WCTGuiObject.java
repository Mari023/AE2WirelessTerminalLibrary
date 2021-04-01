package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import de.mari_023.fabric.ae2wtlib.terminal.WTGUIObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WCTGuiObject extends WTGUIObject implements IPortableCell /*, IViewCellStorage*/ {

    public WCTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
    }
}