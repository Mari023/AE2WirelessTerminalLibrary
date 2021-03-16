package de.mari_023.fabric.ae2wtlib;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.TabButton;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTGuiObject;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTGuiObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.render.item.ItemRenderer;
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
        if(containerTarget instanceof WCTGuiObject) {//TODO don't hardcode
            previousContainerIcon = new ItemStack(ae2wtlib.CRAFTING_TERMINAL);
            previousContainerType = WCTContainer.TYPE;
        } else if(containerTarget instanceof WPTGuiObject) {
            previousContainerIcon = new ItemStack(ae2wtlib.PATTERN_TERMINAL);
            previousContainerType = WPTContainer.TYPE;
        } else {
            previousContainerIcon = null;
            previousContainerType = null;
        }
    }

    public final TabButton addBackButton(Consumer<TabButton> buttonAdder, int x, int y) {
        return addBackButton(buttonAdder, x, y, null);
    }

    public final TabButton addBackButton(Consumer<TabButton> buttonAdder, int x, int y, Text label) {
        if(previousContainerType != null && !previousContainerIcon.isEmpty()) {
            if(label == null) label = previousContainerIcon.getName();
            ItemRenderer itemRenderer = gui.getClient().getItemRenderer();
            TabButton button = new TabButton(gui.getX() + x, gui.getY() + y, previousContainerIcon, label, itemRenderer, btn -> goBack());
            buttonAdder.accept(button);
            return button;
        }
        return null;
    }

    public final void goBack() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registry.SCREEN_HANDLER.getId(previousContainerType));
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "switch_gui"), buf);
    }
}