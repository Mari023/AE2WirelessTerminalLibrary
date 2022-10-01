package de.mari_023.ae2wtlib.terminal;

import java.util.OptionalLong;

import org.jetbrains.annotations.Nullable;

import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.features.Locatables;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.security.IActionHost;
import appeng.core.localization.PlayerMessages;
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

        Level level = player.getCommandSenderWorld();
        if (level.isClientSide()) {
            return false;
        }

        OptionalLong key = getGridKey(item);
        if (key.isEmpty()) {
            player.sendMessage(PlayerMessages.DeviceNotLinked.text(), Util.NIL_UUID);
            return false;
        }

        IActionHost securityStation = Locatables.securityStations().get(level, key.getAsLong());
        if (securityStation == null) {
            player.sendMessage(PlayerMessages.StationCanNotBeLocated.text(), Util.NIL_UUID);
            return false;
        }

        if (!hasPower(player, 0.5, item)) {
            player.sendMessage(PlayerMessages.DeviceNotPowered.text(), Util.NIL_UUID);
            return false;
        }
        return true;
    }

    OptionalLong getGridKey(ItemStack item);

    boolean hasPower(Player player, double amt, ItemStack is);
}
