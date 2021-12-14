package de.mari_023.fabric.ae2wtlib.client;

import appeng.api.IAEAddonEntrypoint;
import com.mojang.blaze3d.platform.InputConstants;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wat.WATMenu;
import de.mari_023.fabric.ae2wtlib.wat.WATScreen;
import de.mari_023.fabric.ae2wtlib.wet.WETMenu;
import de.mari_023.fabric.ae2wtlib.wet.WETScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AE2wtlibclient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        ScreenRegistry.register(WCTMenu.TYPE, WCTScreen::new);
        ScreenRegistry.register(WETMenu.TYPE, WETScreen::new);
        ScreenRegistry.register(WATMenu.TYPE, WATScreen::new);

        NetworkingClient.registerClient();
        registerKeybindings();
    }

    public static void registerKeybindings() {
        KeyMapping wct = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wct", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wpt = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wpt", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wit = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wit", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleRestock = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleRestock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleMagnet = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleMagnet", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkKeybindings(wct, "crafting");
            checkKeybindings(wpt, "pattern_encoding");
            checkKeybindings(wit, "pattern_access");
            checkKeybindings(toggleRestock, "toggleRestock");
            checkKeybindings(toggleMagnet, "toggleMagnet");
        });
    }

    private static void checkKeybindings(KeyMapping binding, String type) {
        while(binding.consumeClick()) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(type);
            ClientPlayNetworking.send(new ResourceLocation(AE2wtlib.MOD_NAME, "hotkey"), buf);
        }
    }
}