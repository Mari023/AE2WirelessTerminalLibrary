package de.mari_023.fabric.ae2wtlib.wut;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        ClientPlayNetworking.send(new Identifier("ae2wtlib", "cycle_terminal"), PacketByteBufs.create());
    }
}