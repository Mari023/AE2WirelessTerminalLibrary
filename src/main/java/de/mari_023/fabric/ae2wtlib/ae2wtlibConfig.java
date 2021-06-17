package de.mari_023.fabric.ae2wtlib;

import appeng.core.AEConfig;
import net.fabricmc.loader.api.FabricLoader;

public class ae2wtlibConfig {

    private static boolean mineMenuChecked, mineMenuPresent, trinketPresent, trinketChecked;

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

    public static boolean allowMineMenu() {
        if(!mineMenuChecked) mineMenuPresent = FabricLoader.getInstance().isModLoaded("minemenufabric");
        mineMenuChecked = true;
        return mineMenuPresent;
    }

    public static boolean allowTrinket() {
        if(!trinketChecked) trinketPresent = isTrinketEnabled() && FabricLoader.getInstance().isModLoaded("trinkets");
        trinketChecked = true;
        return trinketPresent;
    }

    private static boolean isTrinketEnabled() {
        return true;
    }

    public static double magnetCardRange() {
        return 16D;
    }
}