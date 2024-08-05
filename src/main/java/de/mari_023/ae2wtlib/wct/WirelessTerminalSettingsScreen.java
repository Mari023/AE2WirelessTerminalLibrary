package de.mari_023.ae2wtlib.wct;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.TabButton;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.widgets.AECheckbox;
import appeng.menu.SlotSemantics;

import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

public class WirelessTerminalSettingsScreen extends AESubScreen<WCTMenu, WCTScreen> {
    private final AECheckbox pickBlock = widgets.addCheckbox("pickBlock", TextConstants.PICK_BLOCK, () -> stack()
            .set(AE2wtlibComponents.PICK_BLOCK, !stack().getOrDefault(AE2wtlibComponents.PICK_BLOCK, false)));
    private final AECheckbox restock = widgets.addCheckbox("restock", TextConstants.RESTOCK,
            () -> stack().set(AE2wtlibComponents.RESTOCK, !stack().getOrDefault(AE2wtlibComponents.RESTOCK, false)));
    private final AECheckbox magnet = widgets.addCheckbox("magnet", TextConstants.MAGNET,
            () -> stack().set(AE2wtlibAdditionalComponents.MAGNET_SETTINGS,
                    stack().getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF).toggleMagnet()));
    private final AECheckbox pickupToME = widgets.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME,
            () -> stack().set(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, stack()
                    .getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF).togglePickupME()));

    public WirelessTerminalSettingsScreen(WCTScreen parent) {
        super(parent, "/screens/wtlib/wireless_terminal_settings.json");
        widgets.add("back", new TabButton(Icon.BACK, menu.getHost().getMainMenuIcon().getHoverName(), btn -> returnToParent()));
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

    private ItemStack stack() {
        return ((WTMenuHost) getMenu().getHost()).getItemStack();
    }
}
