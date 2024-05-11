package de.mari_023.ae2wtlib.wct;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;

public class ItemWCT extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WCTMenu.TYPE;
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
        if (level.isClientSide())
            return;
        if (!(entity instanceof ServerPlayer player))
            return;
        MagnetHandler.handle(player, itemStack);
    }
}
