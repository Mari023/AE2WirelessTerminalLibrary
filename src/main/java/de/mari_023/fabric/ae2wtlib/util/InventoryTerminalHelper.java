package de.mari_023.fabric.ae2wtlib.util;

import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryTerminalHelper {

    public static ItemStack getTerminal(PlayerEntity player, int slot) {
        ItemStack it;
        if(slot >= 100 && slot < 200 && ae2wtlibConfig.INSTANCE.allowTrinket())
            it = TrinketsApi.getTrinketsInventory(player).getStack(slot - 100);
        else it = player.inventory.getStack(slot);
        return it;
    }
}
