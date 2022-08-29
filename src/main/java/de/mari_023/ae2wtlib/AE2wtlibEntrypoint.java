package de.mari_023.ae2wtlib;

import appeng.api.IAEAddonEntrypoint;
import appeng.menu.locator.MenuLocators;
import de.mari_023.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;

public class AE2wtlibEntrypoint implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        AE2wtlibConfig.init();
        if (Platform.trinketsPresent())
            MenuLocators.register(TrinketLocator.class, TrinketLocator::writeToPacket, TrinketLocator::readFromPacket);
        AE2wtlib.onAe2Initialized();
        var v = MagnetMenu.TYPE;// load the class, so it doesn't happen later (which would crash the server)
    }
}
