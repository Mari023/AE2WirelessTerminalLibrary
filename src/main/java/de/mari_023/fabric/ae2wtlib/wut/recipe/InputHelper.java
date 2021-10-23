package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public final class InputHelper {
    private InputHelper() {
    }

    public static ItemStack getInputStack(CraftingInventory inventory, Ingredient ingredient) {
        for (int i = 0; i < inventory.size(); i++)
            if (ingredient.test(inventory.getStack(i))) return inventory.getStack(i);
        return ItemStack.EMPTY;
    }

    public static int getInputCount(CraftingInventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) if (!inventory.getStack(i).isEmpty()) count++;
        return count;
    }

    public static final Ingredient WUT = Ingredient.ofItems(ae2wtlib.UNIVERSAL_TERMINAL);
}