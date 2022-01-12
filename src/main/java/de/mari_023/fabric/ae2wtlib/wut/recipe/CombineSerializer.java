package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import de.mari_023.fabric.ae2wtlib.AE2wtlib;

public class CombineSerializer extends Serializer<Combine> {
    public static final CombineSerializer INSTANCE = new CombineSerializer();
    public static final String NAME = "combine";
    public static final ResourceLocation ID = new ResourceLocation(AE2wtlib.MOD_NAME, NAME);

    @Override
    public Combine fromJson(ResourceLocation id, JsonObject json) {
        CombineJsonFormat recipeJson = new Gson().fromJson(json, CombineJsonFormat.class);
        if (recipeJson.terminalA == null || recipeJson.terminalB == null || validateOutput(recipeJson.terminalAName)
                || validateOutput(recipeJson.terminalBName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Combine(Ingredient.fromJson(recipeJson.terminalA), Ingredient.fromJson(recipeJson.terminalB),
                recipeJson.terminalAName, recipeJson.terminalBName, id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf packetData, Combine recipe) {
        recipe.getTerminalA().toNetwork(packetData);
        recipe.getTerminalB().toNetwork(packetData);
        packetData.writeUtf(recipe.getTerminalAName());
        packetData.writeUtf(recipe.getTerminalBName());
    }

    @Override
    public Combine fromNetwork(ResourceLocation id, FriendlyByteBuf packetData) {
        return new Combine(Ingredient.fromNetwork(packetData), Ingredient.fromNetwork(packetData),
                packetData.readUtf(32767), packetData.readUtf(32767), id);
    }
}
