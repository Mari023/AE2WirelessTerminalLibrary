package de.mari_023.fabric.ae2wtlib.client;

import java.io.FileNotFoundException;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

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
import net.minecraft.world.inventory.MenuType;

import appeng.api.IAEAddonEntrypoint;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.init.client.InitScreens;
import appeng.menu.AEBaseMenu;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.wat.WATMenu;
import de.mari_023.fabric.ae2wtlib.wat.WATScreen;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wet.WETMenu;
import de.mari_023.fabric.ae2wtlib.wet.WETScreen;

@Environment(EnvType.CLIENT)
public class AE2wtlibclient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        register(WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        register(WETMenu.TYPE, WETScreen::new, "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        register(WATMenu.TYPE, WATScreen::new, "/screens/pattern_access_terminal.json");

        NetworkingClient.registerClient();
        registerKeybindings();
    }

    public static void registerKeybindings() {
        KeyMapping wct = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wct",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wpt = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wpt",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wit = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wit",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleRestock = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleRestock",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleMagnet = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleMagnet",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkKeybindings(wct, "crafting");
            checkKeybindings(wpt, "pattern_encoding");
            checkKeybindings(wit, "pattern_access");
            checkKeybindings(toggleRestock, "toggleRestock");
            checkKeybindings(toggleMagnet, "toggleMagnet");
        });
    }

    private static void checkKeybindings(KeyMapping binding, String type) {
        while (binding.consumeClick()) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(type);
            ClientPlayNetworking.send(new ResourceLocation(AE2wtlib.MOD_NAME, "hotkey"), buf);
        }
    }

    /**
     * Registers a screen for a given menu and ensures the given style is applied after opening the screen.
     * TODO use {@link InitScreens} method for this
     */
    private static <M extends AEBaseMenu, U extends AEBaseScreen<M>> void register(MenuType<M> type,
            InitScreens.StyledScreenFactory<M, U> factory, String stylePath) {
        ScreenRegistry.<M, U>register(type, (menu, playerInv, title) -> {
            ScreenStyle style;
            try {
                style = StyleManager.loadStyleDoc(stylePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to read Screen JSON file: " + stylePath + ": " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read Screen JSON file: " + stylePath, e);
            }

            return factory.create(menu, playerInv, title, style);
        });
    }
}
