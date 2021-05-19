package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Combine implements Recipe<CraftingInventory> {
    private final Ingredient TerminalA;
    private final Ingredient TerminalB;
    private final String TerminalAName;
    private final String TerminalBName;
    private final ItemStack outputStack;
    private final Identifier id;

    public Combine(Ingredient TerminalA, Ingredient TerminalB, String TerminalAName, String TerminalBName, Identifier id) {
        this.TerminalA = TerminalA;
        this.TerminalB = TerminalB;
        this.TerminalAName = TerminalAName;
        this.TerminalBName = TerminalBName;
        this.outputStack = new ItemStack(ae2wtlib.UNIVERSAL_TERMINAL);
        if(!outputStack.hasTag()) outputStack.setTag(new CompoundTag());
        outputStack.getTag().putBoolean(TerminalAName, true);
        outputStack.getTag().putBoolean(TerminalBName, true);
        this.id = id;
    }

    public Ingredient getTerminalA() {
        return TerminalA;
    }

    public Ingredient getTerminalB() {
        return TerminalB;
    }

    public String getTerminalAName() {
        return TerminalAName;
    }

    public String getTerminalBName() {
        return TerminalBName;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return !getInputStack(inv, TerminalA).isEmpty() && !getInputStack(inv, TerminalB).isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return outputStack.copy();
    }

    private ItemStack getInputStack(CraftingInventory inv, Ingredient ingredient) {
        for(int i = 0; i < inv.size(); i++) {
            if(ingredient.test(inv.getStack(i))) return inv.getStack(i);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getOutput() {
        return outputStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CombineRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<Combine> {
        private Type() {}

        public static final Type INSTANCE = new Type();

        public static final String ID = "wut_combine";
    }
}