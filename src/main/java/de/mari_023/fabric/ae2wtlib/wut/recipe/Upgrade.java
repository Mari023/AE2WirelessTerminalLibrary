package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class Upgrade extends Common {
    private final Ingredient terminal;
    private final String terminalName;

    public Upgrade(Ingredient terminal, String terminalName, Identifier id) {
        super(id);
        this.terminal = terminal;
        this.terminalName = terminalName;
        outputStack.getOrCreateNbt().putBoolean(terminalName, true);
    }

    public Ingredient getTerminal() {
        return terminal;
    }

    public String getTerminalName() {
        return terminalName;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT);
        return !InputHelper.getInputStack(inv, terminal).isEmpty() && !wut.isEmpty()
                && InputHelper.getInputCount(inv) == 2 && !WUTHandler.hasTerminal(wut, terminalName);
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {//TODO combine stored energy
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT).copy();
        NbtCompound terminal = InputHelper.getInputStack(inv, this.terminal).getOrCreateNbt().copy();
        wut.getOrCreateNbt().putBoolean(terminalName, true);
        terminal.copyFrom(wut.getNbt());
        wut.setNbt(terminal);

        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UpgradeSerializer.INSTANCE;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(terminal);
        inputs.add(InputHelper.WUT);
        return inputs;
    }
}