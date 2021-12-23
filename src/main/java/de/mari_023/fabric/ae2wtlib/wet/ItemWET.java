package de.mari_023.fabric.ae2wtlib.wet;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AEConfig;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemWET extends ItemWT {

    public ItemWET() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
    }

    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WETMenu.TYPE;
    }

    /*@Nullable
    public ItemMenuHost getMenuHost(Player player, MenuLocator locator, ItemStack stack) {
        Integer slot = null;
        if(locator instanceof TrinketLocator trinketLocator) slot = trinketLocator.itemIndex();

        return new WETMenuHost(player, slot, stack, (p, subMenu) -> tryOpen(p, locator, stack));
    }*/
}