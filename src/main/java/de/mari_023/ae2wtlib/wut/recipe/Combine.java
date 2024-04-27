package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public class Combine extends Common {
    public static final CombineSerializer serializer = new CombineSerializer();
    private final Ingredient terminalA;
    private final Ingredient terminalB;
    private final String terminalAName;
    private final String terminalBName;

    public Combine(Ingredient terminalA, Ingredient terminalB, String terminalAName, String terminalBName) {
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        this.terminalAName = terminalAName;
        this.terminalBName = terminalBName;
        outputStack.set(WUTHandler.wirelessTerminals.get(terminalAName).componentType(), Unit.INSTANCE);
        outputStack.set(WUTHandler.wirelessTerminals.get(terminalBName).componentType(), Unit.INSTANCE);
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
    public boolean matches(CraftingContainer inv, Level world) {
        return !InputHelper.getInputStack(inv, terminalA).isEmpty()
                && !InputHelper.getInputStack(inv, terminalB).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider provider) {
        ItemStack wut = mergeTerminal(outputStack.copy(), InputHelper.getInputStack(inv, terminalA), terminalAName);
        wut = mergeTerminal(wut, InputHelper.getInputStack(inv, terminalB), terminalBName);

        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(terminalA);
        inputs.add(terminalB);
        return inputs;
    }
}
