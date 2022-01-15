package de.mari_023.ae2wtlib.fabric;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.fabric.trinket.TrinketLocator;

import appeng.api.IAEAddonEntrypoint;
import appeng.menu.locator.MenuLocators;

public class AE2wtlibEntrypoint implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MenuLocators.register(TrinketLocator.class, TrinketLocator::writeToPacket, TrinketLocator::readFromPacket);
        AE2wtlib.onAe2Initialized();
    }
}
