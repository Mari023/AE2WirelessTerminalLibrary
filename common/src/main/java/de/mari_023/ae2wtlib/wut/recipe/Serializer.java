package de.mari_023.ae2wtlib.wut.recipe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.crafting.RecipeSerializer;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public interface Serializer<T extends Common> extends RecipeSerializer<T> {
    default boolean validateOutput(@Nullable String s) {
        if (s == null)
            return true;
        return !WUTHandler.terminalNames.contains(s);
    }
}
