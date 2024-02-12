package de.mari_023.ae2wtlib.terminal;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.networking.IGrid;
import appeng.api.util.IConfigManager;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.ConfigManager;

import de.mari_023.ae2wtlib.AE2wtlibItems;

public interface IUniversalWirelessTerminalItem {
    default boolean open(final Player player, final ItemMenuHostLocator locator,
            boolean returningFromSubmenu) {
        return MenuOpener.open(getMenuType(locator, player), player, locator, returningFromSubmenu);
    }

    default boolean tryOpen(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        if (checkUniversalPreconditions(locator.locateItem(player)))
            return open(player, locator, returningFromSubmenu);
        return false;
    }

    MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player);

    @Nullable
    IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Consumer<Component> errorConsumer);

    default boolean checkUniversalPreconditions(ItemStack item) {
        return !item.isEmpty()
                && (item.getItem() == this || item.getItem() == AE2wtlibItems.instance().UNIVERSAL_TERMINAL);
    }

    default IConfigManager getConfigManager(ItemStack target) {
        return new ConfigManager((manager, settingName) -> {
        });
    }

    default void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
    }
}
