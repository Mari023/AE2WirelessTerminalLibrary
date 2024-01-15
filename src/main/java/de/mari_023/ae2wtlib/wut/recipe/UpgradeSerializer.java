package de.mari_023.ae2wtlib.wut.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeSerializer implements net.minecraft.world.item.crafting.RecipeSerializer<Upgrade> {
    public static final String NAME = "upgrade";
    private static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("terminal").forGetter(Upgrade::getTerminal),
                    StringRepresentable.StringRepresentableCodec.STRING.fieldOf("terminalName")
                            .forGetter(Upgrade::getTerminalName))
                    .apply(builder, Upgrade::new));

    @Override
    public Codec<Upgrade> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf packetData, Upgrade recipe) {
        recipe.getTerminal().toNetwork(packetData);
        packetData.writeUtf(recipe.getTerminalName());
    }

    @Override
    public Upgrade fromNetwork(FriendlyByteBuf packetData) {
        return new Upgrade(Ingredient.fromNetwork(packetData), packetData.readUtf(32767));
    }
}
