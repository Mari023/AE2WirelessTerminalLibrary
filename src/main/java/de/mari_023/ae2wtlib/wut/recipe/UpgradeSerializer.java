package de.mari_023.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeSerializer extends Serializer<Upgrade> {
    public static final String NAME = "upgrade";

    public Upgrade fromJson(JsonObject json) {
        UpgradeJsonFormat recipeJson = new Gson().fromJson(json, UpgradeJsonFormat.class);
        if (recipeJson.terminal == null || validateOutput(recipeJson.terminalName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Upgrade(Ingredient.fromJson(recipeJson.terminal, true), recipeJson.terminalName);
    }

    @Override
    public Codec<Upgrade> codec() {
        return null;// FIXME 1.20.2 what does this?
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
