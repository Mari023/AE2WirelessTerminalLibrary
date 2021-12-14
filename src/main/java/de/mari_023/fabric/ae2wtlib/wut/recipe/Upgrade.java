package de.mari_023.fabric.ae2wtlib.wut.recipe;

import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class Upgrade extends Common {
    private final Ingredient terminal;
    private final String terminalName;

    public Upgrade(Ingredient terminal, String terminalName, ResourceLocation id) {
        super(id);
        this.terminal = terminal;
        this.terminalName = terminalName;
        outputStack.getOrCreateTag().putBoolean(terminalName, true);
    }

    public Ingredient getTerminal() {
        return terminal;
    }

    public String getTerminalName() {
        return terminalName;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT);
        return !InputHelper.getInputStack(inv, terminal).isEmpty() && !wut.isEmpty()
                && InputHelper.getInputCount(inv) == 2 && !WUTHandler.hasTerminal(wut, terminalName);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {//TODO combine stored energy
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT).copy();
        CompoundTag terminal = InputHelper.getInputStack(inv, this.terminal).getOrCreateTag().copy();
        wut.getOrCreateTag().putBoolean(terminalName, true);
        terminal.merge(wut.getOrCreateTag());
        wut.setTag(terminal);

        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UpgradeSerializer.INSTANCE;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(terminal);
        inputs.add(InputHelper.WUT);
        return inputs;
    }
}