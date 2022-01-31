package de.mari_023.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeSerializer extends Serializer<Upgrade> {
    public static final UpgradeSerializer INSTANCE = new UpgradeSerializer();
    public static final String NAME = "upgrade";

    @Override
    public Upgrade fromJson(ResourceLocation id, JsonObject json) {
        UpgradeJsonFormat recipeJson = new Gson().fromJson(json, UpgradeJsonFormat.class);
        if (recipeJson.terminal == null || validateOutput(recipeJson.terminalName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Upgrade(Ingredient.fromJson(recipeJson.terminal), recipeJson.terminalName, id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf packetData, Upgrade recipe) {
        recipe.getTerminal().toNetwork(packetData);
        packetData.writeUtf(recipe.getTerminalName());
    }

    @Override
    public Upgrade fromNetwork(ResourceLocation id, FriendlyByteBuf packetData) {
        return new Upgrade(Ingredient.fromNetwork(packetData), packetData.readUtf(32767), id);
    }
}
