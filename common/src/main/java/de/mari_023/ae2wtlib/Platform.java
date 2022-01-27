package de.mari_023.ae2wtlib;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import dev.architectury.injectables.annotations.ExpectPlatform;

import appeng.menu.locator.MenuLocator;

public class Platform {

    @ExpectPlatform
    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Nullable
    public static MenuLocator findTerminal(Player player, String terminalName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab getCreativeModeTab() {
        throw new AssertionError();
    }
}
