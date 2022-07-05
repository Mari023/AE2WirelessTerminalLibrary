package de.mari_023.ae2wtlib.rei;

import net.minecraft.world.item.crafting.RecipeType;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

public class Plugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return AE2wtlib.MOD_NAME;
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(Combine.class, RecipeType.CRAFTING, WUTDisplay::new);
        registry.registerRecipeFiller(Upgrade.class, RecipeType.CRAFTING, WUTDisplay::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING,
                EntryStacks.of(WUTHandler.wirelessTerminals.get("crafting").universalTerminal()));
    }
}
