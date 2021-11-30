package de.mari_023.fabric.ae2wtlib.wat;

import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.BiConsumer;

public class WATMenuHost extends WTMenuHost {

    public WATMenuHost(final PlayerEntity ep, int inventorySlot, final ItemStack is, BiConsumer<PlayerEntity, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return WATMenu.TYPE;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlib.PATTERN_ACCESS_TERMINAL);
    }
}