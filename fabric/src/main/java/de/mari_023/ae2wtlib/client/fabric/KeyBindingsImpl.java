package de.mari_023.ae2wtlib.client.fabric;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class KeyBindingsImpl {
    public static KeyMapping registerKeyBinding(String id) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(id, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
    }

    public static void checkKeybindings(Runnable r) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> r.run());
    }
}
