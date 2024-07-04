package de.mari_023.ae2wtlib.wut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.AEConfig;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.terminal.IconButton;
import de.mari_023.ae2wtlib.terminal.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public interface IUniversalTerminalCapable {
    default void cycleTerminal() {
        storeState();
        PacketDistributor.sendToServer(new CycleTerminalPacket(isHandlingRightClick()));
    }

    WTMenuHost getHost();

    boolean isHandlingRightClick();

    void storeState();

    default boolean checkForTerminalKeys(int keyCode, int scanCode) {
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

    default int getVisibleRows() {
        int availableHeight = getRectangle().height() - 2 * AEConfig.instance().getTerminalMargin();
        var style = Objects.requireNonNull(getStyle().getTerminalStyle());
        return AEConfig.instance().getTerminalStyle().getRows(style.getPossibleRows(availableHeight));
    }

    @UnknownNullability
    ScreenStyle getStyle();

    ScreenRectangle getRectangle();
}
