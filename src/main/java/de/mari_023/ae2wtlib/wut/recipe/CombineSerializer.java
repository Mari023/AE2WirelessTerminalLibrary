package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.serialization.Codec;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

public class CombineSerializer extends Serializer<Combine> {
    public static final String NAME = "combine";
    private static final Codec<Combine> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("terminalA").forGetter(Combine::getTerminalA),
                            Ingredient.CODEC.fieldOf("terminalB").forGetter(Combine::getTerminalB),
                            StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalNameA").forGetter(Combine::getTerminalAName),
                            StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalNameB").forGetter(Combine::getTerminalBName)
                    )
                    .apply(builder, Combine::new)
    );

    @Override
    public Codec<Combine> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf packetData, Combine recipe) {
        recipe.getTerminalA().toNetwork(packetData);
        recipe.getTerminalB().toNetwork(packetData);
        packetData.writeUtf(recipe.getTerminalAName());
        packetData.writeUtf(recipe.getTerminalBName());
    }

    @Override
    public Combine fromNetwork(FriendlyByteBuf packetData) {
        return new Combine(Ingredient.fromNetwork(packetData), Ingredient.fromNetwork(packetData),
                packetData.readUtf(32767), packetData.readUtf(32767));
    }
}
