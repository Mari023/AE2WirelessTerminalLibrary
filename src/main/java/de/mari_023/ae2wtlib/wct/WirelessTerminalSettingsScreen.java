package de.mari_023.ae2wtlib.wct;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlibTags;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.TerminalSettingsPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.SlotSemantics;

public class WirelessTerminalSettingsScreen extends AESubScreen<WCTMenu, WCTScreen> {
    private final AECheckbox pickBlock = widgets.addCheckbox("pickBlock", TextConstants.PICK_BLOCK, this::save);
    private final AECheckbox restock = widgets.addCheckbox("restock", TextConstants.RESTOCK, this::save);
    private final AECheckbox magnet = widgets.addCheckbox("magnet", TextConstants.MAGNET, this::save);
    private final AECheckbox pickupToME = widgets.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME, this::save);

    public WirelessTerminalSettingsScreen(WCTScreen parent) {
        super(parent, "/screens/wtlib/wireless_terminal_settings.json");

        widgets.add("back",
                new TabButton(menu.getHost().getMainMenuIcon(), menu.getHost().getMainMenuIcon().getHoverName(),
                        btn -> returnToParent()));

        CompoundTag itemTag = stack().getOrCreateTag();
        pickBlock.setSelected(itemTag.getBoolean(AE2wtlibTags.PICK_BLOCK));
        restock.setSelected(itemTag.getBoolean(AE2wtlibTags.RESTOCK));
        MagnetMode magnetMode = MagnetMode.fromByte(itemTag.getByte(AE2wtlibTags.MAGNET_SETTINGS));
        magnet.setSelected(magnetMode.magnet());
        pickupToME.setSelected(magnetMode.pickupToME());
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

    private ItemStack stack() {
        return ((WTMenuHost) getMenu().getHost()).getItemStack();
    }

    private void save() {
        ItemStack item = stack();

        item.getOrCreateTag().putBoolean(AE2wtlibTags.PICK_BLOCK, pickBlock.isSelected());
        item.getTag().putBoolean(AE2wtlibTags.RESTOCK, restock.isSelected());
        var magnetSettings = MagnetMode.fromByte(item.getTag().getByte(AE2wtlibTags.MAGNET_SETTINGS));
        magnetSettings = magnetSettings.set(magnet.isSelected(), pickupToME.isSelected());
        item.getTag().putByte(AE2wtlibTags.MAGNET_SETTINGS, magnetSettings.getId());

        ClientNetworkManager.sendToServer(new TerminalSettingsPacket(item.getTag()));
    }
}
