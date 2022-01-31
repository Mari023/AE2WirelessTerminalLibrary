package de.mari_023.ae2wtlib.client.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import de.mari_023.ae2wtlib.client.AE2wtlibClient;

import appeng.api.IAEAddonEntrypoint;

@Environment(EnvType.CLIENT)
public class AE2wtlibClientFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        AE2wtlibClient.onAe2Initialized();
    }
}
