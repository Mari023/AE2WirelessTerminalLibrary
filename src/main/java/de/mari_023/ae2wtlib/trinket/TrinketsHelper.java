package de.mari_023.ae2wtlib.trinket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.wut.WUTHandler;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;

public class TrinketsHelper {

    public static Map<String, Map<String, TrinketInventory>> getTrinketsInventory(Player player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if (optionalComponent.isEmpty())
            return new HashMap<>();
        TrinketComponent component = optionalComponent.get();
        return component.getInventory();
    }

    public static boolean isStillPresent(Player player, ItemStack terminal) {
        return TrinketsApi.getTrinketComponent(player)
                .map(trinketComponent -> trinketComponent.isEquipped(Predicate.isEqual(terminal))).orElse(false);
    }

    public static ItemStack getTrinket(Player player, String group, String type, int slot) {
        var inventory = getTrinketsInventory(player);
        if (inventory.get(group) == null || inventory.get(group).get(type) == null) {
            return ItemStack.EMPTY;
        }
        return inventory.get(group).get(type).getItem(slot);
    }

    @Nullable
    public static TrinketLocator findTerminal(Player player, String terminalName) {
        var inventory = getTrinketsInventory(player);

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

    public static void registerTrinket(Item terminal) {
        if (terminal instanceof Trinket trinket)
            TrinketsApi.registerTrinket(terminal, trinket);
    }

    public static void addAllTrinkets(List<ItemStack> items, Player player) {
        var inventory = getTrinketsInventory(player);
        for (var group : inventory.values()) {
            for (var slotType : group.values()) {
                for (int i = 0; i < slotType.getContainerSize(); i++) {
                    items.add(slotType.getItem(i));
                }
            }
        }
    }
}
