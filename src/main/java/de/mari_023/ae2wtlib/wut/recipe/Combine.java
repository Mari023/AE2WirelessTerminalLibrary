package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public class Combine extends Common {
    private static final MapCodec<Combine> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminalA").forGetter(Combine::getTerminalA),
                    Ingredient.CODEC.fieldOf("terminalB").forGetter(Combine::getTerminalB),
                    WTDefinition.CODEC.fieldOf("terminalAName")
                            .forGetter(Combine::getTerminalADefinition),
                    WTDefinition.CODEC.fieldOf("terminalBName")
                            .forGetter(Combine::getTerminalBDefinition))
                    .apply(builder, Combine::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Combine> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Combine::getTerminalA,
            Ingredient.CONTENTS_STREAM_CODEC, Combine::getTerminalB,
            WTDefinition.STREAM_CODEC, Combine::getTerminalADefinition,
            WTDefinition.STREAM_CODEC, Combine::getTerminalBDefinition,
            Combine::new);
    public static final RecipeSerializer<Combine> serializer = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public static final String NAME = "combine";
    private final Ingredient terminalA;
    private final Ingredient terminalB;
    private final WTDefinition terminalADefinition;
    private final WTDefinition terminalBDefinition;

    public Combine(Ingredient terminalA, Ingredient terminalB, WTDefinition terminalADefinition,
            WTDefinition terminalBDefinition) {
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        this.terminalADefinition = terminalADefinition;
        this.terminalBDefinition = terminalBDefinition;
    }

    public Ingredient getTerminalA() {
        return terminalA;
    }

    public Ingredient getTerminalB() {
        return terminalB;
    }

    public WTDefinition getTerminalADefinition() {
        return terminalADefinition;
    }

    public WTDefinition getTerminalBDefinition() {
        return terminalBDefinition;
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
        return !InputHelper.getInputStack(inv, terminalA).isEmpty()
                && !InputHelper.getInputStack(inv, terminalB).isEmpty() && inv.ingredientCount() == 2;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack wut = mergeTerminal(new ItemStack(AE2wtlibAPI.getWUT()), InputHelper.getInputStack(input, terminalA),
                terminalADefinition);
        wut = mergeTerminal(wut, InputHelper.getInputStack(input, terminalB), terminalBDefinition);

        return wut;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return serializer;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(terminalA);
        inputs.add(terminalB);
        return inputs;
    }
}
