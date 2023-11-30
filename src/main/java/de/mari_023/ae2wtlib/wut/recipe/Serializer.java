package de.mari_023.ae2wtlib.wut.recipe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.crafting.RecipeSerializer;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public abstract class Serializer<T extends Common> implements RecipeSerializer<T> {
    public static boolean validateOutput(@Nullable String s) {
        if (s == null)
            return true;
        return !WUTHandler.terminalNames.contains(s);
    }
}
