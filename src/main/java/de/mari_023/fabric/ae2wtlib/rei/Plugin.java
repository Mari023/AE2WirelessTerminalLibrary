package de.mari_023.fabric.ae2wtlib.rei;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.wut.recipe.Combine;
import de.mari_023.fabric.ae2wtlib.wut.recipe.Upgrade;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.RecipeType;

public class Plugin implements REIClientPlugin {


    @Override
    public String getPluginProviderName() {
        return ae2wtlib.MOD_NAME;
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(Combine.class, RecipeType.CRAFTING, WUTDisplay::new);
        registry.registerRecipeFiller(Upgrade.class, RecipeType.CRAFTING, WUTDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;
        entryRegistry.removeEntry(EntryStacks.of(ae2wtlib.CHECK_TRINKETS));
    }
}