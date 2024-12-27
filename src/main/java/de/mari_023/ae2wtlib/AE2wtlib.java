package de.mari_023.ae2wtlib;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import appeng.api.features.HotkeyAction;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.hotkeys.HotkeyActions;
import appeng.init.client.InitScreens;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.UpgradeHelper;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.hotkeys.MagnetHotkeyAction;
import de.mari_023.ae2wtlib.hotkeys.RestockHotkeyAction;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATMenuHost;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.*;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETMenuHost;
import de.mari_023.ae2wtlib.wet.WETScreen;
import de.mari_023.ae2wtlib.wut.recipe.Combine;
import de.mari_023.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import de.mari_023.ae2wtlib.wut.recipe.UpgradeSerializer;

public class AE2wtlib {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, AE2wtlibAPI.MOD_NAME);

    public static void onAe2Initialized() {
        AddTerminalEvent.register((event -> {
            event.builder("crafting", WCTMenuHost::new, WCTMenu.TYPE, AE2wtlibItems.WIRELESS_CRAFTING_TERMINAL,
                    Icon.CRAFTING)
                    .hotkeyName(HotkeyAction.WIRELESS_TERMINAL)
                    .translationKey("item.ae2.wireless_crafting_terminal").addTerminal();
            event.builder("pattern_encoding", WETMenuHost::new, WETMenu.TYPE, AE2wtlibItems.PATTERN_ENCODING_TERMINAL,
                    Icon.PATTERN_ENCODING)
                    .addTerminal();
            event.builder("pattern_access", WATMenuHost::new, WATMenu.TYPE, AE2wtlibItems.PATTERN_ACCESS_TERMINAL,
                    Icon.PATTERN_ACCESS)
                    .addTerminal();
        }));

        UpgradeHelper.addUpgradeToAllTerminals(AE2wtlibItems.QUANTUM_BRIDGE_CARD, 1);
        Upgrades.add(AE2wtlibItems.MAGNET_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(AE2wtlibItems.MAGNET_CARD, AE2wtlibItems.UNIVERSAL_TERMINAL, 1);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, AE2wtlibAPI.id(UpgradeSerializer.NAME),
                Upgrade.serializer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, AE2wtlibAPI.id(CombineSerializer.NAME),
                Combine.serializer);

        HotkeyActions.register(new RestockHotkeyAction(), "ae2wtlib_restock");
        HotkeyActions.register(new MagnetHotkeyAction(), "ae2wtlib_magnet");
    }

    static void addToCreativeTab() {
        if (AE2wtlibCreativeTab.registrationHappened())
            return;
        for (var t : WTDefinition.wirelessTerminals())
            AE2wtlibCreativeTab.addTerminal(t.item());
        AE2wtlibCreativeTab.addUniversalTerminal(AE2wtlibItems.UNIVERSAL_TERMINAL);
        AE2wtlibCreativeTab.add(AE2wtlibItems.QUANTUM_BRIDGE_CARD);
        AE2wtlibCreativeTab.add(AE2wtlibItems.MAGNET_CARD);
    }

    static void registerMenus() {
        Registry.register(BuiltInRegistries.MENU, WCTMenu.ID, WCTMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, WETMenu.ID, WETMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, WATMenu.ID, WATMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, MagnetMenu.ID, MagnetMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, TrashMenu.ID, TrashMenu.TYPE);
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
}
