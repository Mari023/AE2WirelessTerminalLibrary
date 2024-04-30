package de.mari_023.ae2wtlib.wut;

import java.util.function.BiConsumer;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public record WTDefinition(ContainerOpener containerOpener, WTMenuHostFactory wTMenuHostFactory, MenuType<?> menuType,
        ItemWT item, ItemStack universalTerminal, MutableComponent terminalName, String hotkeyName,
        DataComponentType<Unit> componentType) {
    @FunctionalInterface
    public interface ContainerOpener {
        boolean tryOpen(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu);
    }

    @FunctionalInterface
    public interface WTMenuHostFactory {
        WTMenuHost create(ItemWT item, Player player, ItemMenuHostLocator locator,
                BiConsumer<Player, ISubMenu> returnToMainMenu);
    }
}
