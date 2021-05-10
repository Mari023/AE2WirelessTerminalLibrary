package de.mari_023.fabric.ae2wtlib.client;

import appeng.container.implementations.WirelessCraftConfirmContainer;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
import me.ultrablacklinux.minemenufabric.client.screen.MineMenuSelectScreen;
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
                if(client.player == null) return;

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
                if(client.player == null) return;
                int slot = buf.readInt();
                int count = buf.readInt();
                client.player.inventory.getStack(slot).setCount(count);
                buf.release();
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
            while(wct.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("crafting");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
            }
            while(wpt.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("pattern");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
            }
            while(wit.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("interface");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
            }
            while(toggleRestock.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("toggleRestock");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
                if(MinecraftClient.getInstance().player == null) return;
                CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(MinecraftClient.getInstance().player);
                ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
                if(terminal.isEmpty()) return;
                if(ItemWT.getBoolean(terminal, "restock")) {
                    MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.off").setStyle(Style.EMPTY.withColor(TextColor.parse("red")))), true);
                } else {
                    MinecraftClient.getInstance().player.sendMessage(new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.on").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                }
            }
            while(toggleMagnet.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("toggleMagnet");
                ClientPlayNetworking.send(new Identifier("ae2wtlib", "hotkey"), buf);
            }
        });
    }

    public static void openMineMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;
        if(!(client.currentScreen instanceof MineMenuSelectScreen)) {
            JsonObject menu = new JsonObject();

            JsonObject wct = new JsonObject();
            wct.add("name", new JsonPrimitive("Wireless Crafting Terminal"));
            JsonObject wctIcon = new JsonObject();
            wctIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_crafting_terminal"));
            wctIcon.add("enchanted", new JsonPrimitive(false));
            wctIcon.add("skullOwner", new JsonPrimitive(""));
            wct.add("icon", wctIcon);
            wct.add("type", new JsonPrimitive("ae2wtlib.open"));
            wct.add("data", new JsonPrimitive("crafting"));

            JsonObject wpt = new JsonObject();
            wpt.add("name", new JsonPrimitive("Wireless Pattern Terminal"));
            JsonObject wptIcon = new JsonObject();
            wptIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_pattern_terminal"));
            wptIcon.add("enchanted", new JsonPrimitive(false));
            wptIcon.add("skullOwner", new JsonPrimitive(""));
            wpt.add("icon", wptIcon);
            wpt.add("type", new JsonPrimitive("ae2wtlib.open"));
            wpt.add("data", new JsonPrimitive("pattern"));

            JsonObject wit = new JsonObject();
            wit.add("name", new JsonPrimitive("Wireless Interface Terminal"));
            JsonObject witIcon = new JsonObject();
            witIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_interface_terminal"));
            witIcon.add("enchanted", new JsonPrimitive(false));
            witIcon.add("skullOwner", new JsonPrimitive(""));
            wit.add("icon", witIcon);
            wit.add("type", new JsonPrimitive("ae2wtlib.open"));
            wit.add("data", new JsonPrimitive("interface"));

            menu.add("0", wct);
            menu.add("1", wpt);
            menu.add("2", wit);
            try {
                client.openScreen(new MineMenuSelectScreen(menu, new TranslatableText("minemenu.default.title").getString(), null));
            } catch(NullPointerException e) {
                client.openScreen(null);
                client.player.sendMessage(new TranslatableText("minemenu.error.config"), false);
            }
        }
    }
}