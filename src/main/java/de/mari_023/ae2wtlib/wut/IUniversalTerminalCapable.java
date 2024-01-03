package de.mari_023.ae2wtlib.wut;

import net.neoforged.neoforge.network.PacketDistributor;

import de.mari_023.ae2wtlib.networking.packages.CycleTerminalPacket;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.SERVER.noArg().send(new CycleTerminalPacket(isHandlingRightClick()));
    }

    boolean isHandlingRightClick();

    void storeState();
}
