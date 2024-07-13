package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.world.item.ItemStack;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;

import appeng.api.config.Actionable;
import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.integration.modules.emi.EmiUseCraftingRecipeHandler;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@EmiEntrypoint
public class AE2wtlibEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(WETMenu.TYPE,
                new EmiEncodePatternHandler<>(WETMenu.class));
        registry.addRecipeHandler(WCTMenu.TYPE, new EmiUseCraftingRecipeHandler<>(WCTMenu.class));

        ItemStack wut = WTDefinitions.CRAFTING.universalTerminal();
        AE2wtlibItems.UNIVERSAL_TERMINAL.injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.getAEMaxPower(wut), Actionable.MODULATE);

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING,
                EmiStack.of(wut));
    }
}
