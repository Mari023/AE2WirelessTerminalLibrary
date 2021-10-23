package de.mari_023.fabric.ae2wtlib.wut.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class Combine extends Common {
    private final Ingredient terminalA;
    private final Ingredient terminalB;
    private final String terminalAName;
    private final String terminalBName;

    public Combine(Ingredient terminalA, Ingredient terminalB, String terminalAName, String TerminalBName, Identifier id) {
        super(id);
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        this.terminalAName = terminalAName;
        this.terminalBName = TerminalBName;
        outputStack.getOrCreateNbt().putBoolean(terminalAName, true);
        outputStack.getOrCreateNbt().putBoolean(TerminalBName, true);
    }

    public Ingredient getTerminalA() {
        return terminalA;
    }

    public Ingredient getTerminalB() {
        return terminalB;
    }

    public String getTerminalAName() {
        return terminalAName;
    }

    public String getTerminalBName() {
        return terminalBName;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return !InputHelper.getInputStack(inv, terminalA).isEmpty() && !InputHelper.getInputStack(inv, terminalB).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        NbtCompound terminalA = InputHelper.getInputStack(inv, this.terminalA).getNbt();
        if(terminalA == null) terminalA = new NbtCompound();
        else terminalA = terminalA.copy();

        NbtCompound terminalB = InputHelper.getInputStack(inv, this.terminalB).getNbt();
        if(terminalB == null) terminalB = new NbtCompound();
        else terminalB = terminalB.copy();

        ItemStack wut = outputStack.copy();
        wut.getOrCreateNbt().copyFrom(terminalB).copyFrom(terminalA);
        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CombineSerializer.INSTANCE;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(terminalA);
        inputs.add(terminalB);
        return inputs;
    }
}