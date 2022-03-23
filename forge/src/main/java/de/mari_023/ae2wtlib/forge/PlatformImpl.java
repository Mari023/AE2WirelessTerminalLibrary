package de.mari_023.ae2wtlib.forge;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fml.ModList;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.forge.curio.CurioLocator;
import de.mari_023.ae2wtlib.forge.recipes.ForgeCombineSerializer;
import de.mari_023.ae2wtlib.forge.recipes.ForgeUpgradeSerializer;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import appeng.menu.locator.MenuLocator;

public class PlatformImpl {

    public static boolean trinketsPresent() {
        return ModList.get().isLoaded("curios");
    }

    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        List<SlotResult> slotResults = CuriosApi.getCuriosHelper().findCurios(player, terminal.getItem());
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().equals(terminal)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        if (locator instanceof CurioLocator curioLocator)
            return curioLocator.locateItem(player);
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminalFromAccessory(Player player, String terminalName) {
        List<SlotResult> slotResults = CuriosApi.getCuriosHelper().findCurios(player, AE2wtlib.UNIVERSAL_TERMINAL);
        for (SlotResult slotResult : slotResults) {
            if (WUTHandler.hasTerminal(slotResult.stack(), terminalName)) {
                return new CurioLocator(slotResult.slotContext());
            }
        }

        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, (Item) WUTHandler.wirelessTerminals.get(terminalName).item())
                .map(result -> new CurioLocator(result.slotContext())).orElse(null);
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
