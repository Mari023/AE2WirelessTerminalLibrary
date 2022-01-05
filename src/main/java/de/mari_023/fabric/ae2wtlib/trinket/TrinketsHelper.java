package de.mari_023.fabric.ae2wtlib.trinket;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.HashMap;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrinketsHelper {
    @Deprecated
    public static CombinedTrinketInventory getTrinketsInventory(Player player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return new CombinedTrinketInventory(new HashMap<>());
        TrinketComponent component = optionalComponent.get();
        return new CombinedTrinketInventory(component.getInventory());
    }

    public static ItemStack getTrinket(Player player, String group, String type, int slot) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return ItemStack.EMPTY;
        TrinketComponent component = optionalComponent.get();
        return component.getInventory().get(group).get(type).getItem(slot);
    }
}
