package de.mari_023.fabric.ae2wtlib.client;

import appeng.api.IAEAddonEntrypoint;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wit.WITScreen;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ae2wtlibclient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        ScreenRegistry.register(WCTContainer.TYPE, WCTScreen::new);
        ScreenRegistry.register(WPTContainer.TYPE, WPTScreen::new);
        ScreenRegistry.register(WITContainer.TYPE, WITScreen::new);

        NetworkingClient.registerClient();
        registerKeybindings();
    }

    public static void registerKeybindings() {
        KeyBinding wct = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.wct", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyBinding wpt = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.wpt", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyBinding wit = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.wit", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyBinding toggleRestock = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.toggleRestock", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyBinding toggleMagnet = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.toggleMagnet", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkKeybindings(wct, "crafting");
            checkKeybindings(wpt, "pattern");
            checkKeybindings(wit, "interface");
            checkKeybindings(toggleRestock, "toggleRestock");
            checkKeybindings(toggleMagnet, "toggleMagnet");
        });
    }

    private static void checkKeybindings(KeyBinding binding, String type) {
        while(binding.wasPressed()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(type);
            ClientPlayNetworking.send(new Identifier(ae2wtlib.MOD_NAME, "hotkey"), buf);
        }
    }
}