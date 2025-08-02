package de.mari_023.ae2wtlib.api.terminal;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;

import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.gui.WidgetContainer;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.TextConstants;
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

    void storeState();

    default boolean checkForTerminalKeys(int keyCode, int scanCode) {
        var closingHotkey = Hotkeys.getHotkeyMapping(getHost().getCloseHotkey());
        if (closingHotkey != null && closingHotkey.mapping().matches(keyCode, scanCode)) {
            getHost().getPlayer().closeContainer();
            return true;
        }

        for (var terminal : WTDefinition.wirelessTerminals()) {
            var hotkey = Hotkeys.getHotkeyMapping(terminal.hotkeyName());
            if (hotkey == null)
                continue;
            if (hotkey.mapping().matches(keyCode, scanCode)) {
                PacketDistributor.sendToServer(new HotkeyPacket(hotkey));
                return true;
            }
        }
        return false;
    }

    /**
     * creates the button that switches to the next terminal. you are responsible for adding this to the leftToolbar
     * when appropriate
     * 
     * @return CycleTerminalButton
     */
    @Contract(value = "-> new", pure = true)
    default IconButton cycleTerminalButton() {
        var next = WUTHandler.nextTerminal(getHost().getItemStack(), false);
        var previous = WUTHandler.nextTerminal(getHost().getItemStack(), true);
        return IconButton.withAE2Background(btn -> cycleTerminal(), next.icon())
                .withTooltip(List.of(TextConstants.cycleNext(next), TextConstants.cyclePrevious(previous)));
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
