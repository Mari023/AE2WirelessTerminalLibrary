package de.mari_023.ae2wtlib.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.fabric.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;

import appeng.api.IAEAddonEntrypoint;
import appeng.menu.locator.MenuLocators;

public class AE2wtlibEntrypoint implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        AE2wtlibConfig.init();
        MenuLocators.register(TrinketLocator.class, TrinketLocator::writeToPacket, TrinketLocator::readFromPacket);
        if (AE2wtlibConfig.INSTANCE.allowTrinket())
            Registry.register(Registry.ITEM,
                    new ResourceLocation(AE2wtlib.MOD_NAME, "you_need_to_enable_trinkets_to_join_this_server"),
                    AE2wtlib.CHECK_TRINKETS);
        AE2wtlib.onAe2Initialized();
        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }
}
