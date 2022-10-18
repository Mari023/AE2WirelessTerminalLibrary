package de.mari_023.ae2wtlib;

import appeng.core.localization.GuiText;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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

    public static Item QUANTUM_BRIDGE_CARD;
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

        registerMenus();

        Platform.registerRecipe(UpgradeSerializer.NAME, Upgrade.serializer = new UpgradeSerializer());
        Platform.registerRecipe(CombineSerializer.NAME, Combine.serializer = new CombineSerializer());

        ServerNetworkManager.registerServerBoundPacket(CycleTerminalPacket.NAME, CycleTerminalPacket::new);
        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");

        notifyAddons("");//common
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) notifyAddons(":server");

        addUpgrades();
    }

    public static void createItems() {
        PATTERN_ENCODING_TERMINAL = new ItemWET();
        PATTERN_ACCESS_TERMINAL = new ItemWAT();
        UNIVERSAL_TERMINAL = new ItemWUT();
        QUANTUM_BRIDGE_CARD = Upgrades
                .createUpgradeCardItem(new Item.Properties().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
        MAGNET_CARD = Upgrades.createUpgradeCardItem(new Item.Properties().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));

        Platform.registerItem("quantum_bridge_card", QUANTUM_BRIDGE_CARD);
        Platform.registerItem("magnet_card", MAGNET_CARD);
        Platform.registerItem("wireless_pattern_encoding_terminal", PATTERN_ENCODING_TERMINAL);
        Platform.registerItem("wireless_pattern_access_terminal", PATTERN_ACCESS_TERMINAL);
        Platform.registerItem("wireless_universal_terminal", UNIVERSAL_TERMINAL);

        Platform.registerTrinket(AEItems.WIRELESS_CRAFTING_TERMINAL.asItem());
        Platform.registerTrinket(UNIVERSAL_TERMINAL);

        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    private static void addUpgrades() {
        var terminals = GuiText.WirelessTerminals.getTranslationKey();
        Upgrades.add(AEItems.ENERGY_CARD, UNIVERSAL_TERMINAL, WUTHandler.getUpgradeCardCount());
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ACCESS_TERMINAL, 2, terminals);
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ENCODING_TERMINAL, 2, terminals);

        Upgrades.add(QUANTUM_BRIDGE_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1, terminals);
        Upgrades.add(QUANTUM_BRIDGE_CARD, PATTERN_ENCODING_TERMINAL, 1, terminals);
        Upgrades.add(QUANTUM_BRIDGE_CARD, PATTERN_ACCESS_TERMINAL, 1, terminals);
        Upgrades.add(QUANTUM_BRIDGE_CARD, UNIVERSAL_TERMINAL, 1, terminals);

        Upgrades.add(MAGNET_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(MAGNET_CARD, UNIVERSAL_TERMINAL, 1);
    }

    private static void registerMenus() {
        Platform.registerMenuType(WCTMenu.ID, WCTMenu.TYPE);
        Platform.registerMenuType(WATMenu.ID, WATMenu.TYPE);
        Platform.registerMenuType(WETMenu.ID, WETMenu.TYPE);
        Platform.registerMenuType(MagnetMenu.ID, MagnetMenu.TYPE);
    }

    public static void notifyAddons(String type) {
        var entrypoints = FabricLoader.getInstance().getEntrypointContainers(MOD_NAME + type, IWTLibAddonEntrypoint.class);
        for (var entrypoint : entrypoints) {
            entrypoint.getEntrypoint().onWTLibInitialized();
        }
    }
}
