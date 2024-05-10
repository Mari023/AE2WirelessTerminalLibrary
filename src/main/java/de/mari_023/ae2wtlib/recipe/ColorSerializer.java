package de.mari_023.ae2wtlib.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ColorSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Color> {
    public static final String NAME = "color";
    private static final MapCodec<Color> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminal").forGetter(Color::terminal),
                    Ingredient.CODEC.fieldOf("dye").forGetter(Color::dye),
                    StringRepresentable.StringRepresentableCodec.STRING.fieldOf("color")
                            .forGetter(Color::color),
                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("output")
                            .forGetter(Color::output))
                    .apply(builder, Color::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Color::terminal,
            Ingredient.CONTENTS_STREAM_CODEC, Color::dye,
            ByteBufCodecs.STRING_UTF8, Color::color,
            ByteBufCodecs.holderRegistry(Registries.ITEM), Color::output,
            Color::new);

    @Override
    public MapCodec<Color> codec() {
        return MAP_CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Color> streamCodec() {
        return STREAM_CODEC;
    }
}
