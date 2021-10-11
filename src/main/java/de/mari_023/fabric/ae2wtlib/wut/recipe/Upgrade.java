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
    private final Ingredient Terminal;
    private final String TerminalName;

    public Upgrade(Ingredient Terminal, String TerminalName, Identifier id) {
        super(id);
        this.Terminal = Terminal;
        this.TerminalName = TerminalName;
        if(!outputStack.hasTag()) outputStack.setTag(new NbtCompound());
        outputStack.getTag().putBoolean(TerminalName, true);
    }

    public Ingredient getTerminal() {
        return Terminal;
    }

    public String getTerminalName() {
        return TerminalName;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.wut);
        return !InputHelper.getInputStack(inv, Terminal).isEmpty() && !wut.isEmpty()
                && InputHelper.getInputCount(inv) == 2 && !WUTHandler.hasTerminal(wut, TerminalName);
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.wut).copy();
        NbtCompound terminal = InputHelper.getInputStack(inv, Terminal).getTag().copy();
        wut.getTag().putBoolean(TerminalName, true);
        terminal.copyFrom(wut.getTag());
        wut.setTag(terminal);

        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UpgradeSerializer.INSTANCE;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(Terminal);
        inputs.add(InputHelper.wut);
        return inputs;
    }
}