package de.mari_023.fabric.ae2wtlib.client;

import appeng.container.implementations.WirelessCraftConfirmContainer;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountScreen;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftConfirmScreen;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftingStatusScreen;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTScreen;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wit.WITScreen;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTScreen;
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
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ae2wtlibclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(WCTContainer.TYPE, WCTScreen::new);
        ScreenRegistry.register(WPTContainer.TYPE, WPTScreen::new);
        ScreenRegistry.register(WITContainer.TYPE, WITScreen::new);
        ScreenRegistry.register(WirelessCraftingStatusContainer.TYPE, WirelessCraftingStatusScreen::new);
        ScreenRegistry.register(WirelessCraftAmountContainer.TYPE, WirelessCraftAmountScreen::new);
        ScreenRegistry.register(WirelessCraftConfirmContainer.TYPE, WirelessCraftConfirmScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "interface_terminal"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null)
                    return;
                final Screen screen = MinecraftClient.getInstance().currentScreen;
                if(screen instanceof WITScreen) {
                    CompoundTag tag = buf.readCompoundTag();
                    if(tag != null)
                        ((WITScreen) screen).postUpdate(tag);
                }
                buf.release();
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "update_restock"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if(client.player == null)
                    return;
                client.player.inventory.getStack(buf.readInt()).setCount(buf.readInt());
                buf.release();
            });
        });
        registerKeybindings();
    }

    public static void registerKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RegisterBind("key.ae2wtlib.wct", "crafting");
            RegisterBind("key.ae2wtlib.wpt", "pattern");
            RegisterBind("key.ae2wtlib.wit", "interface");
            RegisterBind("key.ae2wtlib.toggleMagnet", "toggleMagnet");
            while(KeyBindingHelper.registerKeyBinding(new KeyBinding("key.ae2wtlib.toggleRestock", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib")).wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("toggleRestock");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
                if (MinecraftClient.getInstance().player == null)
                    return;
                ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(MinecraftClient.getInstance().player).getCraftingTerminal();
                if (terminal.isEmpty())
                    return;
                if (ItemWT.getBoolean(terminal, "restock"))
                    Send("gui.ae2wtlib.off", "red");
                else
                    Send("gui.ae2wtlib.on", "green");
            }
        });
    }

    public static void Send(String str1, String str2) {
        MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText(str1).setStyle(Style.EMPTY.withColor(TextColor.parse(str2)))), true);
    }

    public static void RegisterBind(String str1, String str2) {
        while (KeyBindingHelper.registerKeyBinding(new KeyBinding(str1, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib")).wasPressed()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(str2);
            ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
        }
    }
}