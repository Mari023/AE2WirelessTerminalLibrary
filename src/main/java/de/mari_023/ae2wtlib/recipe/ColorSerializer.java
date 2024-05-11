package de.mari_023.ae2wtlib.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import appeng.api.util.AEColor;

public class ColorSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Color> {
    public static final String NAME = "color";
    private static final MapCodec<Color> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminal").forGetter(Color::terminal),
                    Ingredient.CODEC.fieldOf("dye").forGetter(Color::dye),
                    AEColor.CODEC.fieldOf("color")
                            .forGetter(Color::color),
                    ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("output")
                            .forGetter(Color::output))
                    .apply(builder, Color::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Color::terminal,
            Ingredient.CONTENTS_STREAM_CODEC, Color::dye,
            AEColor.STREAM_CODEC, Color::color,
            ItemStack.STREAM_CODEC, Color::output,
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