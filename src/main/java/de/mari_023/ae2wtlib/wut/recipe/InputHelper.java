package de.mari_023.ae2wtlib.wut.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

import de.mari_023.ae2wtlib.AE2wtlibItems;

public final class InputHelper {
    private InputHelper() {}

    public static ItemStack getInputStack(CraftingInput input, Ingredient ingredient) {
        for (var stack : input.items()) {
            if (ingredient.test(stack))
                return stack;
        }
        return ItemStack.EMPTY;
    }

    public static final Ingredient WUT = Ingredient.of(AE2wtlibItems.UNIVERSAL_TERMINAL);
}
