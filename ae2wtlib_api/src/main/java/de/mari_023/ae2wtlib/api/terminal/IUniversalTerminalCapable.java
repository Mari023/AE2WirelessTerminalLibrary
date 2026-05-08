package de.mari_023.ae2wtlib.api.terminal;

import java.util.ArrayList;

import de.mari_023.ae2wtlib.api.gui.TerminalSelectionPanel;

import net.minecraft.client.input.KeyEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.gui.WidgetContainer;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public interface IUniversalTerminalCapable {
    WTMenuHost getHost();

    default boolean checkForTerminalKeys(KeyEvent event) {
        var closingHotkey = Hotkeys.getHotkeyMapping(getHost().getCloseHotkey());
        if (closingHotkey != null && closingHotkey.mapping().matches(event)) {
            getHost().getPlayer().closeContainer();
            return true;
        }

        for (var terminal : WTDefinition.wirelessTerminals()) {
            var hotkey = Hotkeys.getHotkeyMapping(terminal.hotkeyName());
            if (hotkey == null)
                continue;
            if (hotkey.mapping().matches(event)) {
                ClientPacketDistributor.sendToServer(new HotkeyPacket(hotkey));
                return true;
            }
        }
        return false;
    }

    /**
     * creates and adds the terminal selection panel
     *
     * @param widgets the WidgetContainer where the widget will be added
     */
    default void addTerminalSelectionPanel(WidgetContainer widgets) {
        widgets.add("terminalSelection", new TerminalSelectionPanel(getHost()));
    }

    /**
     * creates and adds the upgrade panel
     *
     * @param widgets the WidgetContainer where the widget will be added
     * @param menu    the menu corresponding to this screen
     */
    default ScrollingUpgradesPanel addUpgradePanel(WidgetContainer widgets, AEBaseMenu menu) {
        var upgrades = new ArrayList<>(menu.getSlots(AE2wtlibSlotSemantics.SINGULARITY));
        upgrades.addAll(menu.getSlots(SlotSemantics.UPGRADE));
        var panel = new ScrollingUpgradesPanel(upgrades, getHost(), widgets, () -> getHost().getUpgrades());
        widgets.add("scrollingUpgrades", panel);
        return panel;
    }
}
