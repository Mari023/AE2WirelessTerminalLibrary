package de.mari_023.fabric.ae2wtlib.wit;

import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.BiConsumer;

public class WITGuiObject extends WTGuiObject {

    public WITGuiObject(final PlayerEntity ep, int inventorySlot, final ItemStack is, BiConsumer<PlayerEntity, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
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