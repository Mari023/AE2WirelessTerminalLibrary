package de.mari_023.fabric.ae2wtlib.mixin;

import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WCTContainer.class)
public abstract class TrinketWCTContainer implements TrinketPlayerScreenHandler {}
