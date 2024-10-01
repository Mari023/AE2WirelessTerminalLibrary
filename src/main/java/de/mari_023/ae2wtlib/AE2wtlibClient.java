package de.mari_023.ae2wtlib;

import net.minecraft.client.Minecraft;

import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class AE2wtlibClient {
    public static void clientTick() {
        if (Minecraft.getInstance().player == null)
            return;
        CraftingTerminalHandler.getCraftingTerminalHandler(Minecraft.getInstance().player).checkTerminal();
    }
}
