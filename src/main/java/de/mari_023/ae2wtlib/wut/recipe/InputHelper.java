package de.mari_023.ae2wtlib.wut.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import de.mari_023.ae2wtlib.AE2wtlib;

public final class InputHelper {
    private InputHelper() {
    }

    public static ItemStack getInputStack(CraftingContainer inventory, Ingredient ingredient) {
        for (int i = 0; i < inventory.getContainerSize(); i++)
            if (ingredient.test(inventory.getItem(i)))
                return inventory.getItem(i);
        return ItemStack.EMPTY;
    }

    public static int getInputCount(CraftingContainer inventory) {
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++)
            if (!inventory.getItem(i).isEmpty())
                count++;
        return count;
    }

    public static final Ingredient WUT = Ingredient.of(AE2wtlib.UNIVERSAL_TERMINAL);
}
