package de.mari_023.ae2wtlib.wct;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;

import javax.annotation.Nullable;

public class ItemWCT extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WCTMenu.TYPE;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, entity, slot);
        if (level.isClientSide())
            return;
        if (!(entity instanceof ServerPlayer player))
            return;
        MagnetHandler.handle(player, itemStack);
    }
}
