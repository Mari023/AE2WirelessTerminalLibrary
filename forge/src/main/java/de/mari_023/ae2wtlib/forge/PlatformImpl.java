package de.mari_023.ae2wtlib.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.menu.locator.MenuLocator;

public class PlatformImpl {
    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        return false;
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminal(Player player, String terminalName) {
        return null;
    }
}
