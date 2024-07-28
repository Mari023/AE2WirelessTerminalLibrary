package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public class CombineSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Combine> {
    public static final String NAME = "combine";
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

    @Override
    public MapCodec<Combine> codec() {
        return MAP_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Combine> streamCodec() {
        return STREAM_CODEC;
    }
}
