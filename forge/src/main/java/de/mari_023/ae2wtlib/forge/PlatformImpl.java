package de.mari_023.ae2wtlib.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fml.ModList;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.forge.curio.CurioHelper;
import de.mari_023.ae2wtlib.forge.recipes.ForgeCombineSerializer;
import de.mari_023.ae2wtlib.forge.recipes.ForgeUpgradeSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;

import appeng.menu.locator.MenuLocator;

public class PlatformImpl {

    public static boolean trinketsPresent() {
        return ModList.get().isLoaded("curios");
    }

    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        return CurioHelper.isStillPresent(player, terminal);
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        return CurioHelper.getItemStack(player, locator);
    }

    @Nullable
    public static MenuLocator findTerminalFromAccessory(Player player, String terminalName) {
        return CurioHelper.findTerminal(player, terminalName);
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

    private static void registerRecipe(String name, RecipeSerializer<?> serializer) {
        AE2wtlibForge.RECIPES.register(name, () -> serializer);
    }

    public static void registerRecipes() {
        registerRecipe(UpgradeSerializer.NAME, Upgrade.serializer = new ForgeUpgradeSerializer());
        registerRecipe(CombineSerializer.NAME, Combine.serializer = new ForgeCombineSerializer());
    }
}
