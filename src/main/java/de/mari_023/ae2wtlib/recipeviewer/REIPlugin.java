package de.mari_023.ae2wtlib.recipeviewer;

import net.minecraft.world.item.ItemStack;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

import appeng.api.config.Actionable;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.wut.WTDefinitions;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return AE2wtlibAPI.MOD_NAME;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        ItemStack wut = WTDefinitions.CRAFTING.universalTerminal();
        AE2wtlibItems.UNIVERSAL_TERMINAL.injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.getAEMaxPower(wut), Actionable.MODULATE);

        registry.addWorkstations(BuiltinPlugin.CRAFTING,
                EntryStacks.of(wut));
    }
}
