package de.mari_023.ae2wtlib.wut;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;

public record WTDefinition(WUTHandler.ContainerOpener containerOpener, WUTHandler.WTMenuHostFactory wTMenuHostFactory,
        MenuType<?> menuType, IUniversalWirelessTerminalItem item, ItemStack universalTerminal) {
}
