package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class Serializer<T extends Common> implements RecipeSerializer<T> {
    protected boolean validateOutput(String s) {
        if(s == null) return true;
        return !WUTHandler.terminalNames.contains(s);
    }
}