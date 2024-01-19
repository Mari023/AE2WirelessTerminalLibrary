package de.mari_023.ae2wtlib.wat;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.menu.ISubMenu;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class WATMenuHost extends WTMenuHost {
    public WATMenuHost(final Player ep, @Nullable Integer inventorySlot, final ItemStack is,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        readFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlib.PATTERN_ACCESS_TERMINAL);
    }
}
