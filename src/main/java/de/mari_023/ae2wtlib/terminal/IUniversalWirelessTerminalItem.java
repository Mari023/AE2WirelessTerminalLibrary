package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

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
    default boolean open(final Player player, ItemStack stack, final ItemMenuHostLocator locator,
            boolean returningFromSubmenu) {
        return MenuOpener.open(getMenuType(stack), player, locator, returningFromSubmenu);
    }

    default boolean tryOpen(Player player, ItemMenuHostLocator locator, ItemStack stack, boolean returningFromSubmenu) {
        if (checkUniversalPreconditions(stack, player))
            return open(player, stack, locator, returningFromSubmenu);
        return false;
    }

    MenuType<?> getMenuType(ItemStack stack);

    @Nullable
    IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Player sendMessagesTo);

    default boolean checkUniversalPreconditions(ItemStack item, Player player) {
        if (item.isEmpty()
                || (item.getItem() != this && item.getItem() != AE2wtlibItems.instance().UNIVERSAL_TERMINAL)) {
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
