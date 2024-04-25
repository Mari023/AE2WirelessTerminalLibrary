package de.mari_023.ae2wtlib.wut;

import net.neoforged.neoforge.network.PacketDistributor;

import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    boolean isHandlingRightClick();

    void storeState();
}
