package de.mari_023.ae2wtlib.api.registration;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.ItemLike;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

public class UpgradeHelper {// FIXME
    private static volatile boolean readyForUpgrades = false;
    private static final Map<ItemLike, Integer> upgrades = new HashMap<>();

    public static synchronized void addUpgrades() {
        addUpgradeToAllTerminals(AEItems.ENERGY_CARD, 0);
        // addUpgradeToAllTerminals(AE2wtlibItems.QUANTUM_BRIDGE_CARD, 1);

        // Upgrades.add(AE2wtlibItems.MAGNET_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        // Upgrades.add(AE2wtlibItems.MAGNET_CARD, AE2wtlibItems.UNIVERSAL_TERMINAL, 1);

        readyForUpgrades = true;
        for (var upgrade : upgrades.entrySet()) {
            addUpgradeToAllTerminals(upgrade.getKey(), upgrade.getValue());
        }
    }

    /**
     * @param upgradeCard  upgrade
     * @param maxSupported how many upgrades of this type a terminal can have, 0 for maximum
     */
    public static synchronized void addUpgradeToAllTerminals(ItemLike upgradeCard, int maxSupported) {
        if (!readyForUpgrades) {
            upgrades.put(upgradeCard, maxSupported);
            return;
        }
        if (maxSupported == 0) {
            addMaxUpgradesToAllTerminals(upgradeCard);
            return;
        }
        for (var terminal : WTDefinition.wirelessTerminals()) {
            int max = terminal.upgradeCount();
            if (max == 0)
                continue;
            Upgrades.add(upgradeCard, terminal.item(), Math.min(maxSupported, max),
                    GuiText.WirelessTerminals.getTranslationKey());
        }
        // Upgrades.add(upgradeCard, AE2wtlibItems.UNIVERSAL_TERMINAL, maxSupported);
    }

    private static void addMaxUpgradesToAllTerminals(ItemLike upgradeCard) {
        // Upgrades.add(upgradeCard, AE2wtlibItems.UNIVERSAL_TERMINAL, WUTHandler.getUpgradeCardCount());
        for (var terminal : WTDefinition.wirelessTerminals()) {
            int max = terminal.upgradeCount();
            if (max == 0)
                continue;
            Upgrades.add(upgradeCard, terminal.item(), max, GuiText.WirelessTerminals.getTranslationKey());
        }
    }
}
