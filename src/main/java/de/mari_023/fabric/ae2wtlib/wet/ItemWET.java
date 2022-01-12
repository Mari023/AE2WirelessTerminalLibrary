package de.mari_023.fabric.ae2wtlib.wet;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;

public class ItemWET extends ItemWT {

    public ItemWET() {
        super(new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WETMenu.TYPE;
    }
}
