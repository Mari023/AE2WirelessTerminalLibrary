package de.mari_023.ae2wtlib.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.fabric.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;

import appeng.api.IAEAddonEntrypoint;
import appeng.menu.locator.MenuLocators;

public class AE2wtlibEntrypoint implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        AE2wtlibConfig.init();
        if (PlatformImpl.trinketsPresent())
            MenuLocators.register(TrinketLocator.class, TrinketLocator::writeToPacket, TrinketLocator::readFromPacket);
        AE2wtlib.onAe2Initialized();
        var v = MagnetMenu.TYPE;// load the class, so it doesn't happen later (which would crash the server)
        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }
}
