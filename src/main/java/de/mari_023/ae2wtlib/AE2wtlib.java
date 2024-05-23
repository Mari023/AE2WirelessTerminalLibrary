package de.mari_023.ae2wtlib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import appeng.api.features.HotkeyAction;
import appeng.core.definitions.AEItems;
import appeng.hotkeys.HotkeyActions;
import appeng.init.client.InitScreens;

import de.mari_023.ae2wtlib.hotkeys.MagnetHotkeyAction;
import de.mari_023.ae2wtlib.hotkeys.RestockHotkeyAction;
import de.mari_023.ae2wtlib.recipe.*;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATMenuHost;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.*;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETMenuHost;
import de.mari_023.ae2wtlib.wet.WETScreen;
import de.mari_023.ae2wtlib.wut.AddTerminalEvent;

public class AE2wtlib {
    public static final String MOD_NAME = "ae2wtlib";

    public static void onAe2Initialized() {
        AddTerminalEvent.register((event -> {
            event.addTerminal("crafting", AE2wtlibItems.WIRELESS_CRAFTING_TERMINAL::tryOpen, WCTMenuHost::new,
                    WCTMenu.TYPE,
                    AE2wtlibItems.WIRELESS_CRAFTING_TERMINAL, HotkeyAction.WIRELESS_TERMINAL,
                    "item.ae2.wireless_crafting_terminal");
            event.addTerminal("pattern_encoding", AE2wtlibItems.PATTERN_ENCODING_TERMINAL::tryOpen, WETMenuHost::new,
                    WETMenu.TYPE,
                    AE2wtlibItems.PATTERN_ENCODING_TERMINAL);
            event.addTerminal("pattern_access", AE2wtlibItems.PATTERN_ACCESS_TERMINAL::tryOpen, WATMenuHost::new,
                    WATMenu.TYPE,
                    AE2wtlibItems.PATTERN_ACCESS_TERMINAL);
        }));

        AddTerminalEvent.run();

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(UpgradeSerializer.NAME), Upgrade.serializer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(CombineSerializer.NAME), Combine.serializer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(ColorSerializer.NAME), Color.serializer);

        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");

        UpgradeHelper.addUpgrades();
    }

    static void addToCreativeTab() {
        AE2wtlibCreativeTab.addTerminal(AEItems.WIRELESS_CRAFTING_TERMINAL.asItem());
        AE2wtlibCreativeTab.addTerminal(AE2wtlibItems.PATTERN_ENCODING_TERMINAL);
        AE2wtlibCreativeTab.addTerminal(AE2wtlibItems.PATTERN_ACCESS_TERMINAL);
        AE2wtlibCreativeTab.addUniversalTerminal(AE2wtlibItems.UNIVERSAL_TERMINAL);
        AE2wtlibCreativeTab.add(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
        AE2wtlibCreativeTab.add(AE2wtlibItems.MAGNET_CARD);
    }

    @SuppressWarnings("unused")
    static void registerMenus() {
        // TODO find a better way to do this.
        // classloading causes this to be registered by ae2, but I don't know how reliable this is
        var a = MagnetMenu.TYPE;
        var b = WCTMenu.TYPE;
        var c = WETMenu.TYPE;
        var d = WATMenu.TYPE;
        var e = MagnetMenu.TYPE;
        var f = TrashMenu.TYPE;
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        InitScreens.register(event, WETMenu.TYPE, WETScreen::new,
                "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        InitScreens.register(event, WATMenu.TYPE, WATScreen::new,
                "/screens/wtlib/wireless_pattern_access_terminal.json");
        InitScreens.register(event, MagnetMenu.TYPE, MagnetScreen::new, "/screens/wtlib/magnet.json");
        InitScreens.register(event, TrashMenu.TYPE, TrashScreen::new, "/screens/wtlib/trash.json");
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_NAME, name);
    }
}
