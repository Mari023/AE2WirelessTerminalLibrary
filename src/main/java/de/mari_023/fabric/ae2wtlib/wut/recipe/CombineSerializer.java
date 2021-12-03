package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class CombineSerializer extends Serializer<Combine> {
    public static final CombineSerializer INSTANCE = new CombineSerializer();
    public static final String NAME = "combine";
    public static final Identifier ID = new Identifier(AE2wtlib.MOD_NAME, NAME);

    @Override
    public Combine read(Identifier id, JsonObject json) {
        CombineJsonFormat recipeJson = new Gson().fromJson(json, CombineJsonFormat.class);
        if(recipeJson.terminalA == null || recipeJson.terminalB == null || validateOutput(recipeJson.terminalAName) || validateOutput(recipeJson.terminalBName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Combine(Ingredient.fromJson(recipeJson.terminalA), Ingredient.fromJson(recipeJson.terminalB), recipeJson.terminalAName, recipeJson.terminalBName, id);
    }

    @Override
    public void write(PacketByteBuf packetData, Combine recipe) {
        recipe.getTerminalA().write(packetData);
        recipe.getTerminalB().write(packetData);
        packetData.writeString(recipe.getTerminalAName());
        packetData.writeString(recipe.getTerminalBName());
    }

    @Override
    public Combine read(Identifier id, PacketByteBuf packetData) {
        return new Combine(Ingredient.fromPacket(packetData), Ingredient.fromPacket(packetData), packetData.readString(32767), packetData.readString(32767), id);
    }
}