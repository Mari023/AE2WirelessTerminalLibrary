package de.mari_023.fabric.ae2wtlib.wit;

import appeng.api.features.IWirelessTermHandler;
import appeng.container.ContainerLocator;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

public class WITGuiObject extends WTGuiObject {

    public WITGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
    }

    @Override
    public boolean open(PlayerEntity player, ContainerLocator locator) {
        return WITContainer.open(player, locator);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return WITContainer.TYPE;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ae2wtlib.INTERFACE_TERMINAL);
    }
}