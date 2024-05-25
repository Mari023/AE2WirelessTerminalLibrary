package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = AE2wtlib.id("core");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // FIXME 1.20.2
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(WTDefinitions.CRAFTING.universalTerminal(),
                RecipeTypes.CRAFTING);
    }
}
