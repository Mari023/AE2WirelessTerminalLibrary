package de.mari_023.ae2wtlib.wat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemWAT extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WATMenu.TYPE;
    }

    public IConfigManager getConfigManager(ItemStack target) {
        var configManager = super.getConfigManager(target);
        configManager.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        configManager.readFromNBT(target.getOrCreateTag().copy());
        return configManager;
    }
}
