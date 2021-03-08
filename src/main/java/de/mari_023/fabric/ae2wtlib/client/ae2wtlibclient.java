package de.mari_023.fabric.ae2wtlib.client;

import de.mari_023.fabric.ae2wtlib.WCTContainer;
import de.mari_023.fabric.ae2wtlib.WCTScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class ae2wtlibclient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(WCTContainer.TYPE, WCTScreen::new);
    }
}