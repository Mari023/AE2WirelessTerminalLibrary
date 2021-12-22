package de.mari_023.fabric.ae2wtlib.wut;

import net.minecraft.world.inventory.MenuType;

public record WTDefinition(WUTHandler.ContainerOpener containerOpener, WUTHandler.WTMenuHostFactory wTMenuHostFactory, MenuType<?> menuType) {

}
