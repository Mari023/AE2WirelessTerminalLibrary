package de.mari_023.ae2wtlib;

import appeng.core.AEConfig;
import eu.midnightdust.lib.config.MidnightConfig;

@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal" })
public class AE2wtlibConfig extends MidnightConfig {
    @Entry public static double outOfRangePowerMultiplier = 2.0;

    @Entry public static double magnetCardRange = 16.0;

    public static double getOutOfRangePower() {
        return AEConfig.instance().wireless_getDrainRate(AEConfig.instance().wireless_getMaxRange(64))
                * outOfRangePowerMultiplier;
    }

    public static double magnetCardRange() {
        return magnetCardRange;
    }

    private static boolean init = false;

    public static void init() {
        if (init)
            return;
        init = true;
        MidnightConfig.init("ae2wtlib", AE2wtlibConfig.class);
    }
}
