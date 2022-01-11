package de.mari_023.fabric.ae2wtlib.wut.recipe;

import appeng.api.config.Actionable;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class Combine extends Common {
    private final Ingredient terminalA;
    private final Ingredient terminalB;
    private final String terminalAName;
    private final String terminalBName;

    public Combine(Ingredient terminalA, Ingredient terminalB, String terminalAName, String TerminalBName, ResourceLocation id) {
        super(id);
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        this.terminalAName = terminalAName;
        this.terminalBName = TerminalBName;
        outputStack.getOrCreateTag().putBoolean(terminalAName, true);
        outputStack.getOrCreateTag().putBoolean(TerminalBName, true);
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
        return !InputHelper.getInputStack(inv, terminalA).isEmpty() && !InputHelper.getInputStack(inv, terminalB).isEmpty() && InputHelper.getInputCount(inv) == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        CompoundTag terminalA = InputHelper.getInputStack(inv, this.terminalA).getTag();
        if (terminalA == null) terminalA = new CompoundTag();
        else terminalA = terminalA.copy();

        CompoundTag terminalB = InputHelper.getInputStack(inv, this.terminalB).getTag();
        if (terminalB == null) terminalB = new CompoundTag();
        else terminalB = terminalB.copy();

        ItemStack wut = outputStack.copy();

        wut.getOrCreateTag().merge(terminalB).merge(terminalA);

        AEBasePoweredItem item = (AEBasePoweredItem) outputStack.getItem();
        item.extractAEPower(wut, item.getAECurrentPower(wut), Actionable.MODULATE); // clear power
        item.injectAEPower(wut,
                ((AEBasePoweredItem) this.terminalA.getItems()[0].getItem()).getAECurrentPower(this.terminalA.getItems()[0]) +
                        ((AEBasePoweredItem) this.terminalB.getItems()[0].getItem()).getAECurrentPower(this.terminalB.getItems()[0]),
                Actionable.MODULATE
        ); // set power

        return wut;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CombineSerializer.INSTANCE;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(terminalA);
        inputs.add(terminalB);
        return inputs;
    }
}