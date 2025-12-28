package de.mari_023.ae2wtlib.wct;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.SlotSemantics;
import appeng.util.Icon;

import de.mari_023.ae2wtlib.AE2wtlibAdditionalComponents;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.networking.TerminalSettingsPacket;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

public class WirelessTerminalSettingsScreen extends AESubScreen<WCTMenu, WCTScreen> {
    private final AECheckbox pickBlock = widgets.addCheckbox("pickBlock", TextConstants.PICK_BLOCK,
            this::changeVisibility);
    private final AECheckbox craftIfMissing = widgets.addCheckbox("craftIfMissing", TextConstants.CRAFT_IF_MISSING,
            this::save);
    private final AECheckbox restock = widgets.addCheckbox("restock", TextConstants.RESTOCK, this::save);
    private final AECheckbox magnet = widgets.addCheckbox("magnet", TextConstants.MAGNET, this::save);
    private final AECheckbox pickupToME = widgets.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME, this::save);

    public WirelessTerminalSettingsScreen(WCTScreen parent) {
        super(parent, "/screens/wtlib/wireless_terminal_settings.json");
        widgets.add("back",
                new TabButton(Icon.BACK, menu.getHost().getMainMenuIcon().getHoverName(), btn -> returnToParent()));

        pickBlock.setSelected(stack().getOrDefault(AE2wtlibComponents.PICK_BLOCK, false));
        craftIfMissing.setSelected(stack().getOrDefault(AE2wtlibComponents.CRAFT_IF_MISSING, false));
        craftIfMissing.active = pickBlock.isSelected();
        restock.setSelected(stack().getOrDefault(AE2wtlibComponents.RESTOCK, false));
        magnet.setSelected(stack().getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF).magnet());
        pickupToME.setSelected(
                stack().getOrDefault(AE2wtlibAdditionalComponents.MAGNET_SETTINGS, MagnetMode.OFF).pickupToME());
        if (MagnetHandler.getMagnetMode(stack()) == MagnetMode.NO_CARD) {
            magnet.active = false;
            pickupToME.active = false;
        }
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

    private ItemStack stack() {
        return ((WTMenuHost) getMenu().getHost()).getItemStack();
    }

    private void changeVisibility() {
        craftIfMissing.active = pickBlock.isSelected();
        save();
    }

    private void save() {
        var locator = ((WTMenuHost) getMenu().getHost()).getLocator();
        if (locator == null)
            return;
        ClientPacketDistributor.sendToServer(new TerminalSettingsPacket(locator,
                pickBlock.isSelected(), restock.isSelected(), magnet.isSelected(), pickupToME.isSelected(),
                craftIfMissing.isSelected()));
    }
}
