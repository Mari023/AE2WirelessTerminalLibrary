package de.mari_023.fabric.ae2wtlib.wut;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.resources.ResourceLocation;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        var buf = PacketByteBufs.create();
        buf.writeBoolean(isHandlingRightClick());
        ClientPlayNetworking.send(new ResourceLocation(AE2wtlib.MOD_NAME, "cycle_terminal"), buf);
    }

    boolean isHandlingRightClick();
}