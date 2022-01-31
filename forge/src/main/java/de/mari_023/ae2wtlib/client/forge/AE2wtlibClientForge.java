package de.mari_023.ae2wtlib.client.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.client.AE2wtlibClient;

@Mod.EventBusSubscriber(modid = AE2wtlib.MOD_NAME, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AE2wtlibClientForge {

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event) {//doesn't actually run
        event.enqueueWork(AE2wtlibClient::onAe2Initialized);
    }
}
