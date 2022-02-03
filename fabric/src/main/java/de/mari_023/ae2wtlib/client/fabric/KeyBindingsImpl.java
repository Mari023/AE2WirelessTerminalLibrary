package de.mari_023.ae2wtlib.client.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class KeyBindingsImpl {
    public static KeyMapping registerKeyBinding(KeyMapping mapping) {
        KeyBindingHelper.registerKeyBinding(mapping);
        return mapping;
    }

    public static void checkKeybindings(Runnable r) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> r.run());
    }
}
