package de.mari_023.ae2wtlib.client.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import de.mari_023.ae2wtlib.AE2wtlib;

@Mod.EventBusSubscriber(modid = AE2wtlib.MOD_NAME, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientForgeEvents {

    private ClientForgeEvents() {
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (KeyBindingsImpl.isInitialized)
            KeyBindingsImpl.checkKeybindings.run();
    }
}
