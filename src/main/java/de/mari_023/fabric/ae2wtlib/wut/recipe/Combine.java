package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Combine implements CraftingRecipe {
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
        return !InputHelper.getInputStack(inv, TerminalA).isEmpty() && !InputHelper.getInputStack(inv, TerminalB).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return outputStack.copy();
    }

    @Environment(EnvType.CLIENT)
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
}