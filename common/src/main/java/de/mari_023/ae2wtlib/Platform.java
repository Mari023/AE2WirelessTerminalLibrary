package de.mari_023.ae2wtlib;

import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import dev.architectury.injectables.annotations.ExpectPlatform;

import appeng.menu.locator.MenuLocator;

public class Platform {

    @ExpectPlatform
    public static boolean trinketsPresent() {
        throw new AssertionError();
    }

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
    public static MenuLocator findTerminalFromAccessory(Player player, String terminalName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab getCreativeModeTab() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItem(String name, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerRecipes() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean preventRemoteMovement(ItemEntity item) {
        return true;
    }

    @ExpectPlatform
    public static void registerMenuType(String id, MenuType<?> menuType) {
        throw new AssertionError();
    }
}
