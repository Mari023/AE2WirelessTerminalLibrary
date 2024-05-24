package de.mari_023.ae2wtlib.recipeviewer;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WTDefinition;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return AE2wtlib.MOD_NAME;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING,
                EntryStacks.of(WTDefinition.of("crafting").universalTerminal()));
    }
}
