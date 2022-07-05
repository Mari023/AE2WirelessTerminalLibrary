package de.mari_023.ae2wtlib.trinket;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.wut.WUTHandler;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;

public class TrinketsHelper {

    @Nullable
    public static Map<String, Map<String, TrinketInventory>> getTrinketsInventory(Player player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if (optionalComponent.isEmpty())
            return null;
        TrinketComponent component = optionalComponent.get();
        return component.getInventory();
    }

    public static boolean isStillPresent(Player player, ItemStack terminal) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if (optionalComponent.isEmpty())
            return false;
        return optionalComponent.get().isEquipped(Predicate.isEqual(terminal));
    }

    public static ItemStack getTrinket(Player player, String group, String type, int slot) {
        var inventory = getTrinketsInventory(player);
        if (inventory == null)
            return ItemStack.EMPTY;
        return inventory.get(group).get(type).getItem(slot);
    }

    @Nullable
    public static TrinketLocator findTerminal(Player player, String terminalName) {
        var inventory = getTrinketsInventory(player);
        if (inventory == null)
            return null;

        for (Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for (Map.Entry<String, TrinketInventory> slotType : group.getValue().entrySet()) {
                for (int i = 0; i < slotType.getValue().getContainerSize(); i++) {
                    ItemStack stack = slotType.getValue().getItem(i);
                    if (WUTHandler.hasTerminal(stack, terminalName))
                        return new TrinketLocator(group.getKey(), slotType.getKey(), i);
                }
            }
        }
        return null;
    }
}
