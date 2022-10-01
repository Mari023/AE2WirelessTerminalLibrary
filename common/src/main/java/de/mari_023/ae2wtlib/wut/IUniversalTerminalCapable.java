package de.mari_023.ae2wtlib.wut;

import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.CycleTerminalPacket;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        ClientNetworkManager.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    boolean isHandlingRightClick();

    void storeState();
}
