package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.world.item.ItemStack;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;

import appeng.api.config.Actionable;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@EmiEntrypoint
public class AE2wtlibEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        // FIXME 1.21.8
        // registry.addRecipeHandler(WETMenu.TYPE, new EmiEncodePatternHandler<>(WETMenu.class));
        // registry.addRecipeHandler(WCTMenu.TYPE, new EmiUseCraftingRecipeHandler<>(WCTMenu.class));

        ItemStack wut = WTDefinitions.CRAFTING.universalTerminal();
        AE2wtlibItems.UNIVERSAL_TERMINAL.asItem().injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.asItem().getAEMaxPower(wut), Actionable.MODULATE);

        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING,
                EmiStack.of(wut));
    }
}
