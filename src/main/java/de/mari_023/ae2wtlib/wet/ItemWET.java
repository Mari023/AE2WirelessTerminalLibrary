package de.mari_023.ae2wtlib.wet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemWET extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WETMenu.TYPE;
    }
}
