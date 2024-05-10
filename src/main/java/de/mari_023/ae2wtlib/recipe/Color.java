package de.mari_023.ae2wtlib.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import appeng.api.util.AEColor;

import de.mari_023.ae2wtlib.AE2wtlibComponents;

public record Color(Ingredient terminal, Ingredient dye, String color, ItemStack output) implements CraftingRecipe {

    public static final ColorSerializer serializer = new ColorSerializer();
    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        return !InputHelper.getInputStack(inv, terminal).isEmpty()
                && !InputHelper.getInputStack(inv, dye).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider p_336092_) {
        for (ItemStack stack : inv.getItems()) {
            if (terminal.test(stack)) {
                stack = stack.copy();
                stack.set(AE2wtlibComponents.COLOR, AEColor.fromDye(DyeColor.valueOf(color)));
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_336125_) {
        var result = output.copy();
        result.set(AE2wtlibComponents.COLOR, AEColor.fromDye(DyeColor.valueOf(color)));
        return result;
    }

    @Override
    public RecipeSerializer<Color> getSerializer() {
        return serializer;
    }
}
