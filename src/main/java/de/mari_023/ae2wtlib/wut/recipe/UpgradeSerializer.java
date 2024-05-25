package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import de.mari_023.ae2wtlib.wut.WTDefinition;

public class UpgradeSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Upgrade> {
    public static final String NAME = "upgrade";
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

    @Override
    public MapCodec<Upgrade> codec() {
        return MAP_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Upgrade> streamCodec() {
        return STREAM_CODEC;
    }
}
