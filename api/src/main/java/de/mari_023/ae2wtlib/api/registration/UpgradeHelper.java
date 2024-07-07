package de.mari_023.ae2wtlib.api.registration;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;

public class UpgradeHelper {
    private static volatile boolean readyForUpgrades = false;
    private static final Map<ItemLike, Integer> upgrades = new HashMap<>();

    public static synchronized void addUpgrades() {
        addUpgradeToAllTerminals(AEItems.ENERGY_CARD, 0);

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
        addUpgradesToUniversalTerminal(upgradeCard, maxSupported);
    }

    private static void addMaxUpgradesToAllTerminals(ItemLike upgradeCard) {
        addUpgradesToUniversalTerminal(upgradeCard, WUTHandler.getUpgradeCardCount());
        for (var terminal : WTDefinition.wirelessTerminals()) {
            int max = terminal.upgradeCount();
            if (max == 0)
                continue;
            Upgrades.add(upgradeCard, terminal.item(), max, GuiText.WirelessTerminals.getTranslationKey());
        }
    }

    private static void addUpgradesToUniversalTerminal(ItemLike upgradeCard, int maxSupported) {
        Item wut = AE2wtlibAPI.instance().getWUT();
        if (wut != Items.AIR)
            Upgrades.add(upgradeCard, wut, maxSupported);
    }
}
