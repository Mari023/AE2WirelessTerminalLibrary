package de.mari_023.ae2wtlib.client;

import net.minecraft.client.KeyMapping;

import de.mari_023.ae2wtlib.networking.NetworkingManager;
import de.mari_023.ae2wtlib.networking.c2s.HotkeyPacket;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class KeyBindings {
    public static void registerKeyBindings() {
        KeyMapping wct = registerKeyBinding("key.ae2wtlib.wct");
        KeyMapping wpt = registerKeyBinding("key.ae2wtlib.wpt");
        KeyMapping wit = registerKeyBinding("key.ae2wtlib.wit");
        KeyMapping toggleRestock = registerKeyBinding("key.ae2wtlib.toggleRestock");
        KeyMapping toggleMagnet = registerKeyBinding("key.ae2wtlib.toggleMagnet");

        checkKeybindings(() -> {
            checkKeybinding(wct, "crafting");
            checkKeybinding(wpt, "pattern_encoding");
            checkKeybinding(wit, "pattern_access");
            checkKeybinding(toggleRestock, "toggleRestock");
            checkKeybinding(toggleMagnet, "toggleMagnet");
        });
    }

    @ExpectPlatform
    private static KeyMapping registerKeyBinding(String id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void checkKeybindings(Runnable r) {
        throw new AssertionError();
    }

    private static void checkKeybinding(KeyMapping binding, String type) {
        while (binding.consumeClick()) {
            NetworkingManager.sendToServer(new HotkeyPacket(type));
        }
    }
}
