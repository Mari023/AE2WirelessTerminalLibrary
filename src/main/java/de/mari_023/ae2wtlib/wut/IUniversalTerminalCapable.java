package de.mari_023.ae2wtlib.wut;

import java.util.List;

import org.jetbrains.annotations.Contract;

import net.neoforged.neoforge.network.PacketDistributor;

import appeng.client.Hotkeys;
import appeng.client.gui.WidgetContainer;
import appeng.core.network.serverbound.HotkeyPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.slot.AppEngSlot;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.terminal.IconButton;
import de.mari_023.ae2wtlib.terminal.SingularityPanel;
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
     * creates and adds the background for the singularity
     * 
     * @param widgets the WidgetContainer where the widget will be added
     * @param menu    the menu corresponding to this screen
     */
    default void addSingularityPanel(WidgetContainer widgets, AEBaseMenu menu) {
        widgets.add("singularity",
                new SingularityPanel((AppEngSlot) menu.getSlots(AE2wtlibSlotSemantics.SINGULARITY).getFirst(),
                        () -> getHost().getUpgrades()));
    }
}
