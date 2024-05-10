package de.mari_023.ae2wtlib.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Upgrade> {
    public static final String NAME = "upgrade";
    private static final MapCodec<Upgrade> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminal").forGetter(Upgrade::getTerminal),
                    StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalName")
                            .forGetter(Upgrade::getTerminalName))
                    .apply(builder, Upgrade::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Upgrade> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Upgrade::getTerminal,
            ByteBufCodecs.STRING_UTF8, Upgrade::getTerminalName,
            Upgrade::new);

    @Override
    public MapCodec<Upgrade> codec() {
        return MAP_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Upgrade> streamCodec() {
        return STREAM_CODEC;
    }
}
