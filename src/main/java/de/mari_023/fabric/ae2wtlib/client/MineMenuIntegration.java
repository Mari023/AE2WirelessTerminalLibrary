package de.mari_023.fabric.ae2wtlib.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.mari_023.fabric.ae2wtlib.mixin.MineMenuMixin;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import me.ultrablacklinux.minemenufabric.client.screen.MineMenuSelectScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

public class MineMenuIntegration {
    private static JsonObject wct;
    private static JsonObject wpt;
    private static JsonObject wit;

    public static void openMineMenu(ItemStack terminal) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;
        if((client.currentScreen instanceof MineMenuSelectScreen)) return;
        JsonObject menu = new JsonObject();

        int i = 0;
        if(WUTHandler.hasTerminal(terminal, "crafting")) {
            menu.add("" + i, getWCT());
            i++;
        }
        if(WUTHandler.hasTerminal(terminal, "pattern")) {
            menu.add("" + i, getWPT());
            i++;
        }
        if(WUTHandler.hasTerminal(terminal, "interface")) {
            menu.add("" + i, getWIT());
            i++;
        }

        if(i < 2) return;

        try {
            MineMenuSelectScreen screen = new MineMenuSelectScreen(menu, new TranslatableText("item.ae2wtlib.wireless_universal_terminal").getString(), null);
            ((MineMenuMixin) screen).setCircleEntries(i);
            client.openScreen(screen);
        } catch(NullPointerException e) {
            client.openScreen(null);
            client.player.sendMessage(new TranslatableText("minemenu.error.config"), false);
        }
    }

    public static JsonObject getWCT() {
        if(wct == null) {
            wct = new JsonObject();
            wct.add("name", new JsonPrimitive("item.ae2wtlib.wireless_crafting_terminal"));
            wit.add("icon", getIcon("ae2wtlib:wireless_crafting_terminal"));
            wct.add("type", new JsonPrimitive("ae2wtlib.open"));
            wct.add("data", new JsonPrimitive("crafting"));
        }
        return wct;
    }

    public static JsonObject getWPT() {
        if(wpt == null) {
            wpt = new JsonObject();
            wpt.add("name", new JsonPrimitive("item.ae2wtlib.wireless_pattern_terminal"));
            wit.add("icon", getIcon("ae2wtlib:wireless_pattern_terminal"));
            wpt.add("type", new JsonPrimitive("ae2wtlib.open"));
            wpt.add("data", new JsonPrimitive("pattern"));
        }
        return wpt;
    }

    public static JsonObject getWIT() {
        if(wit == null) {
            wit = new JsonObject();
            wit.add("name", new JsonPrimitive("item.ae2wtlib.wireless_interface_terminal"));
            wit.add("icon", getIcon("ae2wtlib:wireless_interface_terminal"));
            wit.add("type", new JsonPrimitive("ae2wtlib.open"));
            wit.add("data", new JsonPrimitive("interface"));
        }
        return wit;
    }

    public static JsonObject getIcon(String iconItem) {
        JsonObject Icon = new JsonObject();
        Icon.add("iconItem", new JsonPrimitive(iconItem));
        Icon.add("enchanted", new JsonPrimitive(false));
        Icon.add("skullOwner", new JsonPrimitive(""));
        Icon.add("customModelData", new JsonPrimitive(0));
        return Icon;
    }
}