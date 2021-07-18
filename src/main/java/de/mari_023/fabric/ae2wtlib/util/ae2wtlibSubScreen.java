package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.TabButton;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ae2wtlibSubScreen {

    private final ScreenHandlerType<?> previousContainerType;
    private final ItemStack previousContainerIcon;

    /**
     * Based on the container we're opening for, try to determine what it's "primary" GUI would be so that we can go
     * back to it.
     **/
    public ae2wtlibSubScreen(Object containerTarget) {
        if(containerTarget instanceof WTGuiObject) {
            previousContainerType = ((WTGuiObject) containerTarget).getType();
            previousContainerIcon = ((WTGuiObject) containerTarget).getIcon();
        } else {
            previousContainerType = null;
            previousContainerIcon = ItemStack.EMPTY;
        }
    }

    public TabButton addBackButton(String id, WidgetContainer widgets) {
        return addBackButton(id, widgets, null);
    }

    public TabButton addBackButton(String id, WidgetContainer widgets, Text label) {
        if(previousContainerType != null && !previousContainerIcon.isEmpty()) {
            if(label == null) label = previousContainerIcon.getName();

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            TabButton button = new TabButton(previousContainerIcon, label, itemRenderer, (btn) -> goBack());
            widgets.add(id, button);
            return button;
        } else return null;
    }

    public final void goBack() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registry.SCREEN_HANDLER.getId(previousContainerType));
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "switch_gui"), buf);
    }
}