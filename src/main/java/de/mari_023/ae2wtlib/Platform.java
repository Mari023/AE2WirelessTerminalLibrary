package de.mari_023.ae2wtlib;

import de.mari_023.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.menu.locator.MenuLocator;

public class Platform {

    public static boolean trinketsPresent() {
        return FabricLoader.getInstance().isModLoaded("data/trinkets");
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

    public static CreativeModeTab getCreativeModeTab() {
        return FabricItemGroupBuilder.build(new ResourceLocation(AE2wtlib.MOD_NAME, "general"),
                () -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL));
    }

    public static void registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new ResourceLocation(AE2wtlib.MOD_NAME, name), item);
    }

    private static void registerRecipe(String name, RecipeSerializer<?> serializer) {
        Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(AE2wtlib.MOD_NAME, name), serializer);
    }

    public static void registerRecipes() {
        registerRecipe(UpgradeSerializer.NAME, Upgrade.serializer = new UpgradeSerializer() {
        });
        registerRecipe(CombineSerializer.NAME, Combine.serializer = new CombineSerializer() {
        });
    }

    public static boolean preventRemoteMovement(ItemEntity item) {
        return false;
    }
}
