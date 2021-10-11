package de.mari_023.fabric.ae2wtlib;

import appeng.core.AEConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;

@me.shedaniel.autoconfig.annotation.Config(name = "ae2wtlib")
public class ae2wtlibConfig implements ConfigData {

    //@ConfigEntry.Gui.Tooltip
    private double chargeRate = 8000.0;
    //@ConfigEntry.Gui.Tooltip
    private double wutChargeRateMultiplier = 1.0;
    @ConfigEntry.Gui.Tooltip
    private double outOfRangePowerMultiplier = 2.0;
    //@ConfigEntry.Gui.Tooltip
    private double magnetCardRange = 16.0;

    @ConfigEntry.Gui.Tooltip(count = 2)
    private boolean enableTrinket = true;

    @ConfigEntry.Gui.Excluded
    public static ae2wtlibConfig INSTANCE;

    @ConfigEntry.Gui.Excluded
    private static boolean mineMenuChecked, mineMenuPresent, trinketPresent, trinketChecked;

    public double getPowerMultiplier(double range, boolean isOutOfRange) {
        if(isOutOfRange)
            return AEConfig.instance().wireless_getDrainRate(528 * getOutOfRangePowerMultiplier());
        return AEConfig.instance().wireless_getDrainRate(range);
    }

    public double getChargeRate() {
        return chargeRate;
    }

    public double WUTChargeRateMultiplier() {
        return wutChargeRateMultiplier;
    }

    private double getOutOfRangePowerMultiplier() {
        return outOfRangePowerMultiplier;
    }

    public static boolean allowMineMenu() {
        if(!mineMenuChecked) mineMenuPresent = FabricLoader.getInstance().isModLoaded("minemenufabric");
        mineMenuChecked = true;
        return mineMenuPresent;
    }

    public boolean allowTrinket() {
        if(!trinketChecked) trinketPresent = isTrinketEnabled() && FabricLoader.getInstance().isModLoaded("trinkets");
        trinketChecked = true;
        return trinketPresent;
    }

    private boolean isTrinketEnabled() {
        return enableTrinket;
    }

    public double magnetCardRange() {
        return magnetCardRange;
    }
}