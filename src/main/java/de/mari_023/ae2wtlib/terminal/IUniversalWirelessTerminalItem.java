package de.mari_023.ae2wtlib.terminal;

import java.util.OptionalLong;

import org.jetbrains.annotations.Nullable;

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
    default boolean open(final Player player, ItemStack stack, final MenuLocator locator) {
        return MenuOpener.open(getMenuType(stack), player, locator);
    }

    default boolean tryOpen(Player player, MenuLocator locator, ItemStack stack) {
        if (checkUniversalPreconditions(stack, player))
            return open(player, stack, locator);
        return false;
    }

    @Nullable
    default ItemMenuHost getMenuHost(Player player, MenuLocator locator, ItemStack stack) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).wTMenuHostFactory().create(player,
                null, stack, (p, subMenu) -> tryOpen(player, locator, stack));
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
            player.sendSystemMessage(PlayerMessages.DeviceNotLinked.text());
            return false;
        }

        IActionHost securityStation = Locatables.securityStations().get(level, key.getAsLong());
        if (securityStation == null) {
            player.sendSystemMessage(PlayerMessages.StationCanNotBeLocated.text());
            return false;
        }

        if (!hasPower(player, 0.5, item)) {
            player.sendSystemMessage(PlayerMessages.DeviceNotPowered.text());
            return false;
        }
        return true;
    }

    OptionalLong getGridKey(ItemStack item);

    boolean hasPower(Player player, double amt, ItemStack is);
}
