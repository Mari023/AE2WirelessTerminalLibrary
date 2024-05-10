package de.mari_023.ae2wtlib.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

public class CombineSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Combine> {
    public static final String NAME = "combine";
    private static final MapCodec<Combine> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminalA").forGetter(Combine::getTerminalA),
                    Ingredient.CODEC.fieldOf("terminalB").forGetter(Combine::getTerminalB),
                    StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalAName")
                            .forGetter(Combine::getTerminalAName),
                    StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalBName")
                            .forGetter(Combine::getTerminalBName))
                    .apply(builder, Combine::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Combine> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Combine::getTerminalA,
            Ingredient.CONTENTS_STREAM_CODEC, Combine::getTerminalB,
            ByteBufCodecs.STRING_UTF8, Combine::getTerminalAName,
            ByteBufCodecs.STRING_UTF8, Combine::getTerminalBName,
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
