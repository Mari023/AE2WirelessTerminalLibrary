package de.mari_023.fabric.ae2wtlib.mixin;

import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import dev.emi.trinkets.api.Trinket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemWT.class)
public abstract class TrinketWT implements Trinket {}