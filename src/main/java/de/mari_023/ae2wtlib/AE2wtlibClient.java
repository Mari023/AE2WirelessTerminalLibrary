package de.mari_023.ae2wtlib;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import net.minecraft.client.Minecraft;

import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AE2wtlibAPI.MOD_NAME, dist = Dist.CLIENT)
public class AE2wtlibClient {
    public AE2wtlibClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void clientTick() {
        if (Minecraft.getInstance().player == null)
            return;
        CraftingTerminalHandler.getCraftingTerminalHandler(Minecraft.getInstance().player).checkTerminal();
    }
}
