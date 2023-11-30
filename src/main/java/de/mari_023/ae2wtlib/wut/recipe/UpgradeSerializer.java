package de.mari_023.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

public class UpgradeSerializer extends Serializer<Upgrade> {
    public static final String NAME = "upgrade";
    private static final Codec<Upgrade> CODEC = ExtraCodecs.adaptJsonSerializer(UpgradeSerializer::fromJson, UpgradeSerializer::toJson);

    private static Upgrade fromJson(JsonElement json) {
        UpgradeJsonFormat recipeJson = new Gson().fromJson(json, UpgradeJsonFormat.class);
        if (recipeJson.terminal == null || validateOutput(recipeJson.terminalName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Upgrade(Ingredient.fromJson(recipeJson.terminal, true), recipeJson.terminalName);
    }

    private static JsonElement toJson(Upgrade recipe) {
        JsonObject json = new JsonObject();
        json.add("terminal", recipe.getTerminal().toJson(false));
        json.addProperty("terminalName", recipe.getTerminalName());
        return json;
    }

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
