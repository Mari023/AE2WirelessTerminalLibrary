package de.mari_023.ae2wtlib;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;

@Mod.EventBusSubscriber(modid = AE2wtlib.MOD_NAME, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase.equals(TickEvent.Phase.START))
            MagnetHandler.doMagnet((ServerPlayer) event.player);
    }
}
