package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.datafixers.util.Unit;
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

import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public class Upgrade extends Common {
    private static final MapCodec<Upgrade> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminal").forGetter(Upgrade::getTerminal),
                    WTDefinition.CODEC.fieldOf("terminalName")
                            .forGetter(Upgrade::getTerminalDefinition))
                    .apply(builder, Upgrade::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Upgrade> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Upgrade::getTerminal,
            WTDefinition.STREAM_CODEC, Upgrade::getTerminalDefinition,
            Upgrade::new);
    public static final RecipeSerializer<Upgrade> serializer = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public static final String NAME = "upgrade";
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
    public ItemStack assemble(CraftingInput input) {
        return mergeTerminal(InputHelper.getInputStack(input, InputHelper.WUT).copy(),
                InputHelper.getInputStack(input, terminal).copy(), terminalDefinition);
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
        inputs.add(terminal);
        inputs.add(InputHelper.WUT);
        return inputs;
    }
}
