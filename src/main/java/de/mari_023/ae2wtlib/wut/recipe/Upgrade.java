package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.datafixers.util.Unit;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.wut.WTDefinition;

public class Upgrade extends Common {
    public static final UpgradeSerializer serializer = new UpgradeSerializer();
    private final Ingredient terminal;
    private final WTDefinition terminalDefinition;

    public Upgrade(Ingredient terminal, WTDefinition terminalDefinition) {
        this.terminal = terminal;
        this.terminalDefinition = terminalDefinition;
        outputStack.set(terminalDefinition.componentType(), Unit.INSTANCE);
    }

    public Ingredient getTerminal() {
        return terminal;
    }

    public WTDefinition getTerminalDefinition() {
        return terminalDefinition;
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        ItemStack wut = InputHelper.getInputStack(inv, InputHelper.WUT);
        return !InputHelper.getInputStack(inv, terminal).isEmpty() && !wut.isEmpty() && inv.ingredientCount() == 2
                && wut.get(terminalDefinition.componentType()) == null;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
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
