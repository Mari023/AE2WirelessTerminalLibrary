package de.mari_023.ae2wtlib.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import de.mari_023.ae2wtlib.networking.NetworkingManager;
import de.mari_023.ae2wtlib.networking.s2c.RestockAmountPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateWUTPackage;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETScreen;

import appeng.api.IAEAddonEntrypoint;
import appeng.init.client.InitScreens;

@Environment(EnvType.CLIENT)
public class AE2wtlibclient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        InitScreens.register(WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        InitScreens.register(WETMenu.TYPE, WETScreen::new, "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        InitScreens.register(WATMenu.TYPE, WATScreen::new, "/screens/pattern_access_terminal.json");

        NetworkingManager.registerClientBoundPacket(UpdateWUTPackage.NAME, UpdateWUTPackage::new);
        NetworkingManager.registerClientBoundPacket(UpdateRestockPacket.NAME, UpdateRestockPacket::new);
        NetworkingManager.registerClientBoundPacket(RestockAmountPacket.NAME, RestockAmountPacket::new);
        KeyBindings.registerKeyBindings();
    }
}
