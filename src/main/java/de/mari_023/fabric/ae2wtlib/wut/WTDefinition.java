package de.mari_023.fabric.ae2wtlib.wut;

import net.minecraft.world.inventory.MenuType;

import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;

public record WTDefinition(WUTHandler.ContainerOpener containerOpener, WUTHandler.WTMenuHostFactory wTMenuHostFactory,
        MenuType<?> menuType, IUniversalWirelessTerminalItem item) {

}
