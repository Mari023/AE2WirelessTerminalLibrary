package de.mari_023.fabric.ae2wtlib.wut;

import appeng.container.AEBaseContainer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "cycle_terminal"), PacketByteBufs.create());
        if(MinecraftClient.getInstance().player == null) return;
        try {
        final ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;

        if(!(screenHandler instanceof AEBaseContainer)) return;

        int locator = ((AEBaseContainer) screenHandler).getLocator().getItemIndex();
        ItemStack item = MinecraftClient.getInstance().player.inventory.getStack(locator);

        if(!(item.getItem() instanceof ItemWUT)) return;
        WUTHandler.cycle(item);
        } catch(Exception ignored) {}
    }
}