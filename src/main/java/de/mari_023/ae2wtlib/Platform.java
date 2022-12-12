package de.mari_023.ae2wtlib;

import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import de.mari_023.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.trinket.TrinketsHelper;

import appeng.core.AppEng;
import appeng.menu.locator.MenuLocator;

public class Platform {

    public static boolean trinketsPresent() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }

    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        return TrinketsHelper.isStillPresent(player, terminal);
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        if (locator instanceof TrinketLocator trinketLocator)
            return trinketLocator.locateItem(player);
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminalFromAccessory(Player player, String terminalName) {
        if (trinketsPresent()) {
            return TrinketsHelper.findTerminal(player, terminalName);
        }
        return null;
    }

    public static void registerItem(String name, Item item) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(AE2wtlib.MOD_NAME, name), item);
    }

    public static void registerRecipe(String name, RecipeSerializer<?> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(AE2wtlib.MOD_NAME, name), serializer);
    }

    public static void registerTrinket(Item terminal) {
        if (trinketsPresent())
            TrinketsHelper.registerTrinket(terminal);
    }

    public static boolean preventRemoteMovement(ItemEntity item) {
        return false;
    }

    public static void registerMenuType(String id, MenuType<?> menuType) {
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(id), menuType);
    }
}
