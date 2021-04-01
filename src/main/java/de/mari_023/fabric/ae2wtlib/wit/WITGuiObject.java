package de.mari_023.fabric.ae2wtlib.wit;

import appeng.api.features.IWirelessTermHandler;
import de.mari_023.fabric.ae2wtlib.terminal.WTGUIObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WITGuiObject extends WTGUIObject {

    public WITGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
    }
}