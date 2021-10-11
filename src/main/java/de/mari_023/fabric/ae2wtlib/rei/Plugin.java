package de.mari_023.fabric.ae2wtlib.rei;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wut.recipe.Common;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.util.Identifier;

public class Plugin implements REIPluginV0 {

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(ae2wtlib.MOD_NAME, "rei");
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerAutoCraftingHandler(new CraftingRecipeTransferHandler(WCTContainer.class));
        recipeHelper.registerAutoCraftingHandler(new PatternRecipeTransferHandler());

        recipeHelper.registerWorkingStations(DefaultPlugin.CRAFTING, EntryStack.create(ae2wtlib.CRAFTING_TERMINAL));
    }


    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(DefaultPlugin.CRAFTING, Common.class, WUTDisplay::new);
    }

    public void registerEntries(EntryRegistry entryRegistry) {
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;
        entryRegistry.removeEntry(EntryStack.create(ae2wtlib.CHECK_TRINKETS));
    }
}