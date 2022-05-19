package de.mari_023.ae2wtlib;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import de.mari_023.ae2wtlib.hotkeys.MagnetHotkeyAction;
import de.mari_023.ae2wtlib.hotkeys.RestockHotkeyAction;
import de.mari_023.ae2wtlib.networking.c2s.CycleTerminalPacket;
import de.mari_023.ae2wtlib.networking.ServerNetworkManager;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wat.ItemWAT;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATMenuHost;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wet.ItemWET;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.api.features.GridLinkables;
import appeng.api.features.HotkeyAction;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.hotkeys.HotkeyActions;
import appeng.items.tools.powered.WirelessTerminalItem;

public class AE2wtlib {
    public static final String MOD_NAME = "ae2wtlib";

    public static final CreativeModeTab ITEM_GROUP = Platform.getCreativeModeTab();

    public static ItemWET PATTERN_ENCODING_TERMINAL;
    public static ItemWAT PATTERN_ACCESS_TERMINAL;
    public static ItemWUT UNIVERSAL_TERMINAL;

    public static Item INFINITY_BOOSTER;
    public static Item MAGNET_CARD;

    public static void onAe2Initialized() {
        createItems();

        WUTHandler.addTerminal("crafting",
                ((IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem())::tryOpen,
                WCTMenuHost::new, WCTMenu.TYPE,
                (IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem(),
                HotkeyAction.WIRELESS_TERMINAL);

        WUTHandler.addTerminal("pattern_encoding", PATTERN_ENCODING_TERMINAL::tryOpen, WETMenuHost::new, WETMenu.TYPE,
                PATTERN_ENCODING_TERMINAL);
        WUTHandler.addTerminal("pattern_access", PATTERN_ACCESS_TERMINAL::tryOpen, WATMenuHost::new, WATMenu.TYPE,
                PATTERN_ACCESS_TERMINAL);

        addUpgrades();// TODO add an entrypoint for addons to register their terminals before this

        Platform.registerRecipes();

        ServerNetworkManager.registerServerBoundPacket(CycleTerminalPacket.NAME, CycleTerminalPacket::new);
        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");
    }

    public static void createItems() {
        PATTERN_ENCODING_TERMINAL = new ItemWET();
        PATTERN_ACCESS_TERMINAL = new ItemWAT();
        UNIVERSAL_TERMINAL = new ItemWUT();
        INFINITY_BOOSTER = Upgrades
                .createUpgradeCardItem(new Item.Properties().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
        MAGNET_CARD = Upgrades.createUpgradeCardItem(new Item.Properties().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));

        Platform.registerItem("infinity_booster_card", INFINITY_BOOSTER);
        Platform.registerItem("magnet_card", MAGNET_CARD);
        Platform.registerItem("wireless_pattern_encoding_terminal", PATTERN_ENCODING_TERMINAL);
        Platform.registerItem("wireless_pattern_access_terminal", PATTERN_ACCESS_TERMINAL);
        Platform.registerItem("wireless_universal_terminal", UNIVERSAL_TERMINAL);

        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    private static void addUpgrades() {
        Upgrades.add(AEItems.ENERGY_CARD, UNIVERSAL_TERMINAL, WUTHandler.terminalNames.size() * 2);
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ACCESS_TERMINAL, 2);
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ENCODING_TERMINAL, 2);

        Upgrades.add(INFINITY_BOOSTER, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, PATTERN_ENCODING_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, PATTERN_ACCESS_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, UNIVERSAL_TERMINAL, 1);

        Upgrades.add(MAGNET_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(MAGNET_CARD, UNIVERSAL_TERMINAL, 1);
    }
}
