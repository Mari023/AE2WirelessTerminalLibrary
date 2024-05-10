package de.mari_023.ae2wtlib.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import de.mari_023.ae2wtlib.AE2wtlib;

@EventBusSubscriber(modid = AE2wtlib.MOD_NAME, bus = EventBusSubscriber.Bus.MOD)
public class AE2wtlibDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getVanillaPack(true);
        var existingFileHelper = event.getExistingFileHelper();

        pack.addProvider(packOutput -> new TexturesProvider(packOutput, existingFileHelper));
        pack.addProvider(packOutput -> new ItemModelProvider(packOutput, existingFileHelper));
    }
}
