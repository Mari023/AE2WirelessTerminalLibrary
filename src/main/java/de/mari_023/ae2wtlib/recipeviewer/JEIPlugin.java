package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

import appeng.api.config.Actionable;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = AE2wtlibAPI.id("core");

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
        ItemStack wut = WTDefinitions.CRAFTING.universalTerminal();
        AE2wtlibItems.UNIVERSAL_TERMINAL.asItem().injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.asItem().getAEMaxPower(wut), Actionable.MODULATE);

        registry.addRecipeCatalyst(wut,
                RecipeTypes.CRAFTING);
    }
}
