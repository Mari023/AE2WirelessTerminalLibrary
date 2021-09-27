package de.mari_023.fabric.ae2wtlib.wut.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Combine extends Common {
    private final Ingredient TerminalA;
    private final Ingredient TerminalB;
    private final String TerminalAName;
    private final String TerminalBName;

    public Combine(Ingredient TerminalA, Ingredient TerminalB, String TerminalAName, String TerminalBName, Identifier id) {
        super(id);
        this.TerminalA = TerminalA;
        this.TerminalB = TerminalB;
        this.TerminalAName = TerminalAName;
        this.TerminalBName = TerminalBName;
        outputStack.getOrCreateNbt().putBoolean(TerminalAName, true);
        outputStack.getOrCreateNbt().putBoolean(TerminalBName, true);
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
        return !InputHelper.getInputStack(inv, TerminalA).isEmpty() && !InputHelper.getInputStack(inv, TerminalB).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        NbtCompound terminalA = InputHelper.getInputStack(inv, TerminalA).getNbt();
        if(terminalA == null) terminalA = new NbtCompound();
        else terminalA = terminalA.copy();

        NbtCompound terminalB = InputHelper.getInputStack(inv, TerminalB).getNbt();
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
}