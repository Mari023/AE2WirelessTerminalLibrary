package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

public interface IUniversalWirelessTerminalItem {
    default boolean open(final Player player, ItemStack stack, final MenuLocator locator,
            boolean returningFromSubmenu) {
        return MenuOpener.open(getMenuType(stack), player, locator, returningFromSubmenu);
    }

    default boolean tryOpen(Player player, MenuLocator locator, ItemStack stack, boolean returningFromSubmenu) {
        if (checkUniversalPreconditions(stack, player))
            return open(player, stack, locator, returningFromSubmenu);
        return false;
    }

    @Nullable
    default ItemMenuHost getMenuHost(Player player, MenuLocator locator, ItemStack stack) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).wTMenuHostFactory().create(player,
                null, stack, (p, subMenu) -> tryOpen(player, locator, stack, true));
    }

    MenuType<?> getMenuType(ItemStack stack);

    default boolean checkUniversalPreconditions(ItemStack item, Player player) {
        if (item.isEmpty() || (item.getItem() != this && item.getItem() != AE2wtlib.UNIVERSAL_TERMINAL)) {
            return false;
        }

        return player.level() instanceof ServerLevel;
    }
}
