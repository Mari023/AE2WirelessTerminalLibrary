package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.IGrid;
import appeng.api.util.IConfigManager;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.util.ConfigManager;

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

    @Nullable
    IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Player sendMessagesTo);

    default boolean checkUniversalPreconditions(ItemStack item, Player player) {
        if (item.isEmpty() || (item.getItem() != this && item.getItem() != AE2wtlib.UNIVERSAL_TERMINAL)) {
            return false;
        }

        if (player.level().isClientSide())
            return false;

        return getLinkedGrid(item, player.level(), player) != null;
    }

    default IConfigManager getConfigManager(ItemStack target) {
        return new ConfigManager((manager, settingName) -> {
        });
    }

    default void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
    }
}
