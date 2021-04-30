package de.mari_023.fabric.ae2wtlib;

import appeng.core.AEConfig;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    public static double getPowerMultiplier(double range, boolean isOutOfRange) {
        if(isOutOfRange)
            return AEConfig.instance().wireless_getDrainRate(528 * getOutOfRangePowerMultiplier());
        return AEConfig.instance().wireless_getDrainRate(range);
    }

    public static double getChargeRate() {
        return 8000;
    }

    public static double WUTChargeRateMultiplier() {
        return 1;
    }

    private static int getOutOfRangePowerMultiplier() {
        return 2;
    }

    private static boolean trinketPresent = false;
    private static boolean trinketChecked = false;

    public static boolean allowTrinket() {
        if(!trinketChecked) {
            trinketPresent = isTrinketEnabled() && FabricLoader.getInstance().isModLoaded("trinkets");
        }
        trinketChecked = true;
        return trinketPresent;
    }

    private static boolean isTrinketEnabled() {
        return true;
    }
}