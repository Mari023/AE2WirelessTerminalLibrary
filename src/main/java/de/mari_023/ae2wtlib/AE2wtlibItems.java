package de.mari_023.ae2wtlib;

import static de.mari_023.ae2wtlib.AE2wtlib.id;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessTerminalItem;

import de.mari_023.ae2wtlib.wat.ItemWAT;
import de.mari_023.ae2wtlib.wct.ItemWCT;
import de.mari_023.ae2wtlib.wet.ItemWET;
import de.mari_023.ae2wtlib.wut.ItemWUT;

public final class AE2wtlibItems {
    private AE2wtlibItems() {}

    public static final ItemWCT WIRELESS_CRAFTING_TERMINAL = (ItemWCT) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem();
    public static final ItemWET PATTERN_ENCODING_TERMINAL = new ItemWET();
    public static final ItemWAT PATTERN_ACCESS_TERMINAL = new ItemWAT();
    public static final ItemWUT UNIVERSAL_TERMINAL = new ItemWUT();

    public static final Item QUANTUM_BRIDGE_CARD = Upgrades.createUpgradeCardItem(new Item.Properties().stacksTo(1));
    public static final Item MAGNET_CARD = Upgrades.createUpgradeCardItem(new Item.Properties().stacksTo(1));

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, id("quantum_bridge_card"), QUANTUM_BRIDGE_CARD);
        Registry.register(BuiltInRegistries.ITEM, id("magnet_card"), MAGNET_CARD);
        Registry.register(BuiltInRegistries.ITEM, id("wireless_pattern_encoding_terminal"),
                PATTERN_ENCODING_TERMINAL);
        Registry.register(BuiltInRegistries.ITEM, id("wireless_pattern_access_terminal"),
                PATTERN_ACCESS_TERMINAL);
        Registry.register(BuiltInRegistries.ITEM, id("wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }
}
