package de.mari_023.ae2wtlib;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

@Mod(value = AE2wtlibAPI.MOD_NAME, dist = Dist.CLIENT)
public class AE2wtlibClient {
    public AE2wtlibClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, AE2wtlibClientConfig.SPEC,
                AE2wtlibAPI.MOD_NAME + "-client.toml");
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static void clientTick() {
        if (Minecraft.getInstance().player == null)
            return;
        CraftingTerminalHandler.getCraftingTerminalHandler(Minecraft.getInstance().player).checkTerminal();
    }

    public static void mouseScroll(InputEvent.MouseScrollingEvent event) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null || minecraft.screen != null || !player.isShiftKeyDown() || event.getScrollDeltaY() == 0)
            return;

        if (!(player.getMainHandItem().getItem() instanceof ItemWUT)
                && !(player.getOffhandItem().getItem() instanceof ItemWUT))
            return;

        ClientPacketDistributor.sendToServer(new CycleTerminalPacket(event.getScrollDeltaY() < 0));
        event.setCanceled(true);
    }
}
