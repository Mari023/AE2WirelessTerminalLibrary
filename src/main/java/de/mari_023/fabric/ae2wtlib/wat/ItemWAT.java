package de.mari_023.fabric.ae2wtlib.wat;

import appeng.core.AEConfig;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ItemWAT extends ItemWT {

    public ItemWAT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WATMenu.TYPE;
    }
}