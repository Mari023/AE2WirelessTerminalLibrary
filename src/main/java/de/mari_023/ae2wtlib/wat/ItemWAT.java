package de.mari_023.ae2wtlib.wat;

import java.util.function.Supplier;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.terminal.AE2wtlibConfigManager;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;

public class ItemWAT extends ItemWT {
    public ItemWAT(Properties p) {
        super(p);
    }

    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WATMenu.TYPE;
    }

    public IConfigManager getConfigManager(Supplier<ItemStack> target) {
        return AE2wtlibConfigManager.builder(target)
                .registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE)
                .build();
    }
}
