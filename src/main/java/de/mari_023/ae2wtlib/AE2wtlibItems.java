package de.mari_023.ae2wtlib;

import static de.mari_023.ae2wtlib.api.AE2wtlibAPI.MOD_NAME;

import appeng.core.definitions.ItemDefinition;
import net.minecraft.world.item.Item;

import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessTerminalItem;

import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.wat.ItemWAT;
import de.mari_023.ae2wtlib.wet.ItemWET;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class AE2wtlibItems {
    private AE2wtlibItems() {
    }

    public static final DeferredRegister.Items DR = DeferredRegister.createItems(MOD_NAME);

    public static final ItemDefinition<WirelessTerminalItem> WIRELESS_CRAFTING_TERMINAL = AEItems.WIRELESS_CRAFTING_TERMINAL;
    public static final ItemDefinition<ItemWET> PATTERN_ENCODING_TERMINAL = item("wireless_pattern_encoding_terminal", ItemWET::new);
    public static final ItemDefinition<ItemWAT> PATTERN_ACCESS_TERMINAL = item("wireless_pattern_access_terminal", ItemWAT::new);
    public static final ItemDefinition<ItemWUT> UNIVERSAL_TERMINAL = item("wireless_universal_terminal", ItemWUT::new);

    public static final ItemDefinition<Item> QUANTUM_BRIDGE_CARD = item("quantum_bridge_card", p -> Upgrades.createUpgradeCardItem(p.stacksTo(1)));
    public static final ItemDefinition<Item> MAGNET_CARD = item("magnet_card", p -> Upgrades.createUpgradeCardItem(p.stacksTo(1)));

    public static void init() {//FIXME 1.21.8
        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    private static <T extends Item> ItemDefinition<T> item(String name, Function<Item.Properties, T> factory) {
        return new ItemDefinition<>(name, DR.registerItem(name, factory));
    }
}
