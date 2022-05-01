package de.mari_023.ae2wtlib.client;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.HotkeyPacket;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class KeyBindings {
    public static void registerKeyBindings() {
        KeyMapping toggleRestock = createBinding("key.ae2wtlib.toggleRestock");
        KeyMapping toggleMagnet = createBinding("key.ae2wtlib.toggleMagnet");

        checkKeybindings(() -> {
            checkKeybinding(toggleRestock, "toggleRestock");
            checkKeybinding(toggleMagnet, "toggleMagnet");
        });
    }

    public static KeyMapping createBinding(String id) {
        return registerKeyBinding(
                new KeyMapping(id, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
    }

    @ExpectPlatform
    private static KeyMapping registerKeyBinding(KeyMapping mapping) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void checkKeybindings(Runnable r) {
        throw new AssertionError();
    }

    private static void checkKeybinding(KeyMapping binding, String type) {
        while (binding.consumeClick()) {
            ClientNetworkManager.sendToServer(new HotkeyPacket(type));
        }
    }
}
