package de.mari_023.ae2wtlib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;

import appeng.api.features.HotkeyAction;
import appeng.core.definitions.AEItems;
import appeng.hotkeys.HotkeyActions;
import appeng.init.client.InitScreens;

import de.mari_023.ae2wtlib.hotkeys.MagnetHotkeyAction;
import de.mari_023.ae2wtlib.hotkeys.RestockHotkeyAction;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATMenuHost;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.*;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETMenuHost;
import de.mari_023.ae2wtlib.wet.WETScreen;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;

public class AE2wtlib {
    public static final String MOD_NAME = "ae2wtlib";

    public static void onAe2Initialized() {
        NeoForge.EVENT_BUS.post(new WUTHandler.AddTerminalEvent());

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(UpgradeSerializer.NAME), Upgrade.serializer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id(CombineSerializer.NAME), Combine.serializer);

        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");

        UpgradeHelper.addUpgrades();
    }

    public static void registerTerminals(WUTHandler.AddTerminalEvent event) {
        AE2wtlibItems items = AE2wtlibItems.instance();
        event.addTerminal("crafting", items.WIRELESS_CRAFTING_TERMINAL::tryOpen, WCTMenuHost::new, WCTMenu.TYPE,
                items.WIRELESS_CRAFTING_TERMINAL, HotkeyAction.WIRELESS_TERMINAL,
                "item.ae2.wireless_crafting_terminal");
        event.addTerminal("pattern_encoding", items.PATTERN_ENCODING_TERMINAL::tryOpen, WETMenuHost::new,
                WETMenu.TYPE,
                items.PATTERN_ENCODING_TERMINAL);
        event.addTerminal("pattern_access", items.PATTERN_ACCESS_TERMINAL::tryOpen, WATMenuHost::new, WATMenu.TYPE,
                items.PATTERN_ACCESS_TERMINAL);
    }

    static void addToCreativeTab() {
        AE2wtlibItems items = AE2wtlibItems.instance();
        AE2wtlibCreativeTab.addTerminal(AEItems.WIRELESS_CRAFTING_TERMINAL.asItem());
        AE2wtlibCreativeTab.addTerminal(items.PATTERN_ENCODING_TERMINAL);
        AE2wtlibCreativeTab.addTerminal(items.PATTERN_ACCESS_TERMINAL);
        AE2wtlibCreativeTab.addTerminal(items.UNIVERSAL_TERMINAL);
        AE2wtlibCreativeTab.add(items.QUANTUM_BRIDGE_CARD);
        AE2wtlibCreativeTab.add(items.MAGNET_CARD);
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

    public static void registerScreens() {
        InitScreens.register(WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        InitScreens.register(WETMenu.TYPE, WETScreen::new, "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        InitScreens.register(WATMenu.TYPE, WATScreen::new, "/screens/wtlib/wireless_pattern_access_terminal.json");
        InitScreens.register(MagnetMenu.TYPE, MagnetScreen::new, "/screens/wtlib/magnet.json");
        InitScreens.register(TrashMenu.TYPE, TrashScreen::new, "/screens/wtlib/trash.json");
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_NAME, name);
    }
}
