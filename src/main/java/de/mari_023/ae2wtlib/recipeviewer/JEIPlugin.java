package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.resources.Identifier;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final Identifier ID = AE2wtlibAPI.id("core");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // FIXME 1.20.2
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(RecipeTypes.CRAFTING, WTDefinitions.CRAFTING.universalTerminalStackWithEnergy());
    }
}
