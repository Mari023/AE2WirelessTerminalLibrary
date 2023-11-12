package de.mari_023.ae2wtlib.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import de.mari_023.ae2wtlib.AE2wtlib;

@Mod.EventBusSubscriber(modid = AE2wtlib.MOD_NAME, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {

    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        AE2wtlibClient.onAe2Initialized();
    }
}
