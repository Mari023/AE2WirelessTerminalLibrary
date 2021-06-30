package de.mari_023.fabric.ae2wtlib.util;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.TabButton;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public final class ae2wtlibSubScreen {

    private final AEBaseScreen<?> gui;
    private final ScreenHandlerType<?> previousContainerType;
    private final ItemStack previousContainerIcon;

    /**
     * Based on the container we're opening for, try to determine what it's "primary" GUI would be so that we can go
     * back to it.
     */
    public ae2wtlibSubScreen(AEBaseScreen<?> gui, Object containerTarget) {
        this.gui = gui;

        if(containerTarget instanceof WTGuiObject) {
            previousContainerType = ((WTGuiObject) containerTarget).getType();
            previousContainerIcon = ((WTGuiObject) containerTarget).getIcon();
        } else {
            previousContainerType = null;
            previousContainerIcon = ItemStack.EMPTY;
        }
    }

    public final TabButton addBackButton(Consumer<TabButton> buttonAdder, int x, int y) {
        return addBackButton(buttonAdder, x, y, null);
    }

    public final TabButton addBackButton(Consumer<TabButton> buttonAdder, int x, int y, Text label) {
        if(previousContainerIcon.isEmpty()) return null;
        if(label == null) label = previousContainerIcon.getName();
        TabButton button = new TabButton(gui.getGuiLeft() + x, gui.getGuiTop() + y, previousContainerIcon, label, gui.getMinecraft().getItemRenderer(), btn -> goBack());
        buttonAdder.accept(button);
        return button;
    }

    public final void goBack() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registry.SCREEN_HANDLER.getId(previousContainerType));
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "switch_gui"), buf);
    }
}