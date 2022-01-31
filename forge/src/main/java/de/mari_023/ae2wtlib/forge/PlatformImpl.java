package de.mari_023.ae2wtlib.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import de.mari_023.ae2wtlib.AE2wtlib;

import appeng.menu.locator.MenuLocator;

public class PlatformImpl {
    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        return false;
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminalFromAccessory(Player player, String terminalName) {
        return null;
    }

    public static CreativeModeTab getCreativeModeTab() {
        return new CreativeModeTab(AE2wtlib.MOD_NAME) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL);
            }
        };
    }

    public static void registerItem(String name, Item item) {
        AE2wtlibForge.ITEMS.register(name, () -> item);
    }

    public static void registerRecipe(String name, RecipeSerializer<?> serializer) {
        AE2wtlibForge.RECIPES.register(name, () -> serializer);
    }
}
