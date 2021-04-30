package de.mari_023.fabric.ae2wtlib.wit;

import appeng.api.features.IWirelessTermHandler;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WITGuiObject extends WTGuiObject {

    public WITGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot, WITContainer.TYPE);
    }
}