package de.mari_023.ae2wtlib.wut;

import de.mari_023.ae2wtlib.networking.NetworkingManager;
import de.mari_023.ae2wtlib.networking.c2s.CycleTerminalPacket;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        NetworkingManager.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    boolean isHandlingRightClick();
}
