package de.mari_023.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class CombineSerializer extends Serializer<Combine> {
    public static final String NAME = "combine";

    public Combine fromJson(JsonObject json) {
        CombineJsonFormat recipeJson = new Gson().fromJson(json, CombineJsonFormat.class);
        if (recipeJson.terminalA == null || recipeJson.terminalB == null || validateOutput(recipeJson.terminalAName)
                || validateOutput(recipeJson.terminalBName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Combine(Ingredient.fromJson(recipeJson.terminalA, true),
                Ingredient.fromJson(recipeJson.terminalB, true),
                recipeJson.terminalAName, recipeJson.terminalBName);
    }

    @Override
    public Codec<Combine> codec() {
        return null;// FIXME 1.20.2 what does this?
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
