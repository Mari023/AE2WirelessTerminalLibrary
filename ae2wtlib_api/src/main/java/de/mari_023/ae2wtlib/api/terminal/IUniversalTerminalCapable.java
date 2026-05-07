package de.mari_023.ae2wtlib.api.terminal;

import java.util.ArrayList;

import org.jetbrains.annotations.Contract;

import net.minecraft.client.input.KeyEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.gui.WidgetContainer;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.gui.IconButton;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        AE2wtlibAPI.cycleTerminal(isHandlingRightClick());
    }

    WTMenuHost getHost();

    boolean isHandlingRightClick();

    @SuppressWarnings("EmptyMethod")
    void storeState();

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
     * creates the button that opens the terminal selector. you are responsible for adding this to the leftToolbar when
     * appropriate
     *
     * @return TerminalSelectionButton
     */
    @Contract(value = "-> new", pure = true)
    default IconButton cycleTerminalButton() {
        return new TerminalSelectionButton(getHost(), this::storeState);
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
