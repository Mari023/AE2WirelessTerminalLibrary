package de.mari_023.ae2wtlib.recipeviewer;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;

import de.mari_023.ae2wtlib.wut.WTDefinitions;

@EmiEntrypoint
public class AE2wtlibEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        // FIXME 1.21.8
        // registry.addRecipeHandler(WETMenu.TYPE, new EmiEncodePatternHandler<>(WETMenu.class));
        // registry.addRecipeHandler(WCTMenu.TYPE, new EmiUseCraftingRecipeHandler<>(WCTMenu.class));

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(WTDefinitions.CRAFTING.universalTerminalStackWithEnergy()));
    }
}
