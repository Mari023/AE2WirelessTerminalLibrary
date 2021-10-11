package de.mari_023.fabric.ae2wtlib.rei;

import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import me.shedaniel.rei.api.AutoTransferHandler;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.plugin.DefaultPlugin;

public class PatternRecipeTransferHandler extends RecipeTransferHandler<WPTContainer> {

    PatternRecipeTransferHandler() {
        super(WPTContainer.class);
    }

    protected AutoTransferHandler.Result doTransferRecipe(WPTContainer container, RecipeDisplay recipe) {
        if(container.isCraftingMode() && recipe.getRecipeCategory() != DefaultPlugin.CRAFTING)
            return AutoTransferHandler.Result.createFailed("jei.appliedenergistics2.requires_processing_mode");
        if(recipe.getResultingEntries().isEmpty())
            return AutoTransferHandler.Result.createFailed("jei.appliedenergistics2.no_output");
        return null;
    }

    @Override
    protected boolean isCrafting() {
        return false;
    }
}