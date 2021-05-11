package de.mari_023.fabric.ae2wtlib.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.mari_023.fabric.ae2wtlib.mixin.MineMenuMixin;
import me.ultrablacklinux.minemenufabric.client.screen.MineMenuSelectScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class MineMenuIntegration {
    private static JsonObject wct;
    private static JsonObject wpt;
    private static JsonObject wit;

    public static void openMineMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;
        if(!(client.currentScreen instanceof MineMenuSelectScreen)) {
            JsonObject menu = new JsonObject();

            menu.add("0", getWCT());
            menu.add("1", getWPT());
            menu.add("2", getWIT());
            try {
                MineMenuSelectScreen screen = new MineMenuSelectScreen(menu, new TranslatableText("minemenu.default.title").getString(), null);
                ((MineMenuMixin) screen).setCircleEntries(3);
                client.openScreen(screen);
            } catch(NullPointerException e) {
                client.openScreen(null);
                client.player.sendMessage(new TranslatableText("minemenu.error.config"), false);
            }
        }
    }

    public static JsonObject getWCT() {
        if(wct == null) {
            wct = new JsonObject();
            wct.add("name", new JsonPrimitive("Wireless Crafting Terminal"));
            JsonObject wctIcon = new JsonObject();
            wctIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_crafting_terminal"));
            wctIcon.add("enchanted", new JsonPrimitive(false));
            wctIcon.add("skullOwner", new JsonPrimitive(""));
            wct.add("icon", wctIcon);
            wct.add("type", new JsonPrimitive("ae2wtlib.open"));
            wct.add("data", new JsonPrimitive("crafting"));
        }
        return wct;
    }

    public static JsonObject getWPT() {
        if(wpt == null) {
            wpt = new JsonObject();
            wpt.add("name", new JsonPrimitive("Wireless Pattern Terminal"));
            JsonObject wptIcon = new JsonObject();
            wptIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_pattern_terminal"));
            wptIcon.add("enchanted", new JsonPrimitive(false));
            wptIcon.add("skullOwner", new JsonPrimitive(""));
            wpt.add("icon", wptIcon);
            wpt.add("type", new JsonPrimitive("ae2wtlib.open"));
            wpt.add("data", new JsonPrimitive("pattern"));
        }
        return wpt;
    }

    public static JsonObject getWIT() {
        if(wit == null) {
            wit = new JsonObject();
            wit.add("name", new JsonPrimitive("Wireless Interface Terminal"));
            JsonObject witIcon = new JsonObject();
            witIcon.add("iconItem", new JsonPrimitive("ae2wtlib:wireless_interface_terminal"));
            witIcon.add("enchanted", new JsonPrimitive(false));
            witIcon.add("skullOwner", new JsonPrimitive(""));
            wit.add("icon", witIcon);
            wit.add("type", new JsonPrimitive("ae2wtlib.open"));
            wit.add("data", new JsonPrimitive("interface"));
        }
        return wit;
    }
}