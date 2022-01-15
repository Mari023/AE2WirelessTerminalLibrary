package de.mari_023.ae2wtlib.forge;

import net.minecraftforge.fml.common.Mod;

import de.mari_023.ae2wtlib.AE2wtlib;

@Mod(AE2wtlib.MOD_NAME)
public class AE2wtlibForge {
    public AE2wtlibForge() {
        AE2wtlib.onAe2Initialized();
    }
}
