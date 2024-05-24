package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.wut.WTDefinition;

public class Upgrade extends Common {
    public static final UpgradeSerializer serializer = new UpgradeSerializer();
    private final Ingredient terminal;
    private final WTDefinition terminalDefinition;

    public Upgrade(Ingredient terminal, String terminalName) {
        this.terminal = terminal;
        this.terminalDefinition = WTDefinition.of(terminalName);
        outputStack.set(terminalDefinition.componentType(), Unit.INSTANCE);
    }

    public Ingredient getTerminal() {
        return terminal;
    }

    public String getTerminalName() {
        return terminalDefinition.terminalName();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT);
        return !InputHelper.getInputStack(inv, terminal).isEmpty() && !wut.isEmpty()
                && InputHelper.getInputCount(inv) == 2
                && wut.get(terminalDefinition.componentType()) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider provider) {
        return mergeTerminal(InputHelper.getInputStack(inv, InputHelper.WUT).copy(),
                InputHelper.getInputStack(inv, terminal).copy(), terminalDefinition);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(terminal);
        inputs.add(InputHelper.WUT);
        return inputs;
    }
}
