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

public class Upgrade implements CraftingRecipe {
    private final Ingredient Terminal;
    private final String TerminalName;
    private final ItemStack outputStack;
    private final Identifier id;

    public Upgrade(Ingredient Terminal, String TerminalName, Identifier id) {
        this.Terminal = Terminal;
        this.TerminalName = TerminalName;
        this.outputStack = new ItemStack(ae2wtlib.UNIVERSAL_TERMINAL);
        if(!outputStack.hasTag()) outputStack.setTag(new CompoundTag());
        outputStack.getTag().putBoolean(TerminalName, true);
        this.id = id;
    }

    public Ingredient getTerminal() {
        return Terminal;
    }

    public String getTerminalName() {
        return TerminalName;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return !InputHelper.getInputStack(inv, Terminal).isEmpty() && !InputHelper.getInputStack(inv, Ingredient.ofItems(ae2wtlib.UNIVERSAL_TERMINAL)).isEmpty() && InputHelper.getInputCount(inv) == 2;
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
        return UpgradeRecipeSerializer.INSTANCE;
    }
}