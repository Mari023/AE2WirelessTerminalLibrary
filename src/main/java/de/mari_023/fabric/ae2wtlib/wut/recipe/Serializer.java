package de.mari_023.fabric.ae2wtlib.wut.recipe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.crafting.RecipeSerializer;

import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;

public abstract class Serializer<T extends Common> implements RecipeSerializer<T> {
    protected boolean validateOutput(@Nullable String s) {
        if (s == null)
            return true;
        return !WUTHandler.terminalNames.contains(s);
    }
}
