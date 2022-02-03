package de.mari_023.ae2wtlib.client.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class KeyBindingsImpl {

    public static Runnable checkKeybindings;
    public static boolean isInitialized;

    public static KeyMapping registerKeyBinding(KeyMapping mapping) {
        ClientRegistry.registerKeyBinding(mapping);
        return mapping;
    }

    public static void checkKeybindings(Runnable r) {
        checkKeybindings = r;
        isInitialized = true;
    }
}
