package de.mari_023.ae2wtlib.fabric;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.fabric.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.fabric.trinket.TrinketsHelper;

import appeng.menu.locator.MenuLocator;

public class PlatformImpl {
    public static boolean isStillPresentTrinkets(Player player, ItemStack terminal) {
        return TrinketsHelper.isStillPresent(player, terminal);
    }

    public static ItemStack getItemStackFromTrinketsLocator(Player player, MenuLocator locator) {
        if (locator instanceof TrinketLocator trinketLocator)
            return trinketLocator.locateItem(player);
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminal(Player player, String terminalName) {
        if (AE2wtlibConfig.INSTANCE.allowTrinket()) {
            return TrinketsHelper.findTerminal(player, terminalName);
        }
        return null;
    }
}
