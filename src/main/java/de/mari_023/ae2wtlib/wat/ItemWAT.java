package de.mari_023.ae2wtlib.wat;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.ItemWT;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;

public class ItemWAT extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WATMenu.TYPE;
    }

    public IConfigManager getConfigManager(ItemStack target) {
        var configManager = super.getConfigManager(target);
        configManager.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        configManager.readFromNBT(target.getOrCreateTag().copy());
        return configManager;
    }
}
