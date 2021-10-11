package de.mari_023.fabric.ae2wtlib.client;

import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wit.WITScreen;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTScreen;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ae2wtlibclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(WCTContainer.TYPE, WCTScreen::new);
        ScreenRegistry.register(WPTContainer.TYPE, WPTScreen::new);
        ScreenRegistry.register(WITContainer.TYPE, WITScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "interface_terminal"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null) return;

                final Screen screen = MinecraftClient.getInstance().currentScreen;
                if(screen instanceof WITScreen) {
                    NbtCompound tag = buf.readNbt();
                    if(tag != null)
                        ((WITScreen) screen).postUpdate(false, tag);
                }
                buf.release();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "update_restock"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null) return;
                client.player.inventory.getStack(buf.readInt()).setCount(buf.readInt());
                buf.release();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "update_wut"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null) return;
                int slot = buf.readInt();
                ItemStack is;
                NbtCompound tag = buf.readNbt();
                if(slot >= 100 && slot < 200 && Config.allowTrinket())
                    is = TrinketsApi.getTrinketsInventory(client.player).getStack(slot - 100);
                else is = client.player.inventory.getStack(slot);
                is.setTag(tag);
                buf.release();
                CraftingTerminalHandler.getCraftingTerminalHandler(client.player).invalidateCache();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "restock_amounts"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null) return;
                CraftingTerminalHandler ctHandler = CraftingTerminalHandler.getCraftingTerminalHandler(client.player);
                List<AEItemStack> items = new ArrayList<>();
                while(buf.isReadable()) items.add(AEItemStack.fromPacket(buf));
                ctHandler.setRestockAbleItems(items);
            });
        });

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