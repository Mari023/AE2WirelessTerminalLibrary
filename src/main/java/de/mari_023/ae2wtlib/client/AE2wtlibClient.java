package de.mari_023.ae2wtlib.client;

import appeng.api.IAEAddonEntrypoint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.s2c.RestockAmountPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateWUTPackage;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTScreen;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETScreen;

import appeng.init.client.InitScreens;

@Environment(EnvType.CLIENT)
public class AE2wtlibClient implements IAEAddonEntrypoint {
    public void onAe2Initialized() {
        InitScreens.register(WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        InitScreens.register(WETMenu.TYPE, WETScreen::new, "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        InitScreens.register(WATMenu.TYPE, WATScreen::new, "/screens/pattern_access_terminal.json");
        InitScreens.register(MagnetMenu.TYPE, MagnetScreen::new, "/screens/wtlib/magnet.json");

        ClientNetworkManager.registerClientBoundPacket(UpdateWUTPackage.NAME, UpdateWUTPackage::new);
        ClientNetworkManager.registerClientBoundPacket(UpdateRestockPacket.NAME, UpdateRestockPacket::new);
        ClientNetworkManager.registerClientBoundPacket(RestockAmountPacket.NAME, RestockAmountPacket::new);
    }
}
