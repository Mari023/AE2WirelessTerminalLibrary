package de.mari_023.ae2wtlib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import de.mari_023.ae2wtlib.curio.CurioHelper;
import de.mari_023.ae2wtlib.hotkeys.MagnetHotkeyAction;
import de.mari_023.ae2wtlib.hotkeys.RestockHotkeyAction;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wat.ItemWAT;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATMenuHost;
import de.mari_023.ae2wtlib.wct.TrashMenu;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wet.ItemWET;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;

import appeng.api.features.GridLinkables;
import appeng.api.features.HotkeyAction;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.hotkeys.HotkeyActions;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.server.testplots.TestPlots;
import appeng.util.SearchInventoryEvent;

public class AE2wtlib {
    public static final String MOD_NAME = "ae2wtlib";

    public static ItemWET PATTERN_ENCODING_TERMINAL;
    public static ItemWAT PATTERN_ACCESS_TERMINAL;
    public static ItemWUT UNIVERSAL_TERMINAL;

    public static Item QUANTUM_BRIDGE_CARD;
    public static Item MAGNET_CARD;

    public static void onAe2Initialized() {
        WUTHandler.addTerminal("crafting",
                ((IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem())::tryOpen,
                WCTMenuHost::new, WCTMenu.TYPE,
                (IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem(),
                HotkeyAction.WIRELESS_TERMINAL, "item.ae2.wireless_crafting_terminal");

        WUTHandler.addTerminal("pattern_encoding", PATTERN_ENCODING_TERMINAL::tryOpen, WETMenuHost::new, WETMenu.TYPE,
                PATTERN_ENCODING_TERMINAL);
        WUTHandler.addTerminal("pattern_access", PATTERN_ACCESS_TERMINAL::tryOpen, WATMenuHost::new, WATMenu.TYPE,
                PATTERN_ACCESS_TERMINAL);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, makeID(UpgradeSerializer.NAME), Upgrade.serializer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, makeID(CombineSerializer.NAME), Combine.serializer);

        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");

        SearchInventoryEvent.EVENT.register(CurioHelper::addAllCurios);
        // we need something to call addon terminals here

        UpgradeHelper.addUpgrades();
        TestPlots.addPlotClass(AE2WTLibTestPlots.class);
    }

    public static void createItems() {
        PATTERN_ENCODING_TERMINAL = new ItemWET();
        PATTERN_ACCESS_TERMINAL = new ItemWAT();
        UNIVERSAL_TERMINAL = new ItemWUT();
        QUANTUM_BRIDGE_CARD = Upgrades
                .createUpgradeCardItem(new Item.Properties().stacksTo(1));
        MAGNET_CARD = Upgrades.createUpgradeCardItem(new Item.Properties().stacksTo(1));

        Registry.register(BuiltInRegistries.ITEM, makeID("quantum_bridge_card"), QUANTUM_BRIDGE_CARD);
        Registry.register(BuiltInRegistries.ITEM, makeID("magnet_card"), MAGNET_CARD);
        Registry.register(BuiltInRegistries.ITEM, makeID("wireless_pattern_encoding_terminal"),
                PATTERN_ENCODING_TERMINAL);
        Registry.register(BuiltInRegistries.ITEM, makeID("wireless_pattern_access_terminal"),
                PATTERN_ACCESS_TERMINAL);
        Registry.register(BuiltInRegistries.ITEM, makeID("wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    static void addToCreativeTab() {
        AE2WTLibCreativeTab.addTerminal(AEItems.WIRELESS_CRAFTING_TERMINAL.asItem());
        AE2WTLibCreativeTab.addTerminal(PATTERN_ENCODING_TERMINAL);
        AE2WTLibCreativeTab.addTerminal(PATTERN_ACCESS_TERMINAL);
        AE2WTLibCreativeTab.addTerminal(UNIVERSAL_TERMINAL);
        AE2WTLibCreativeTab.add(QUANTUM_BRIDGE_CARD);
        AE2WTLibCreativeTab.add(MAGNET_CARD);
    }

    static void registerMenus() {
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(WCTMenu.ID), WCTMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(WATMenu.ID), WATMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(WETMenu.ID), WETMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(MagnetMenu.ID), MagnetMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId(TrashMenu.ID), TrashMenu.TYPE);
    }

    public static ResourceLocation makeID(String name) {
        return new ResourceLocation(MOD_NAME, name);
    }
}
