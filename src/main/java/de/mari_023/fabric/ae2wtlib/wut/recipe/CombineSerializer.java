package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class CombineSerializer extends Serializer<Combine> {
    public static final CombineSerializer INSTANCE = new CombineSerializer();

    public static final Identifier ID = new Identifier("ae2wtlib", "combine");

    @Override
    public Combine read(Identifier id, JsonObject json) {
        CombineJsonFormat recipeJson = new Gson().fromJson(json, CombineJsonFormat.class);
        if(recipeJson.TerminalA == null || recipeJson.TerminalB == null || validateOutput(recipeJson.TerminalAName) || validateOutput(recipeJson.TerminalBName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        Ingredient TerminalA = Ingredient.fromJson(recipeJson.TerminalA);
        Ingredient TerminalB = Ingredient.fromJson(recipeJson.TerminalB);

        return new Combine(TerminalA, TerminalB, recipeJson.TerminalAName, recipeJson.TerminalBName, id);
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
        Ingredient TerminalA = Ingredient.fromPacket(packetData);
        Ingredient TerminalB = Ingredient.fromPacket(packetData);
        String TerminalAName = packetData.readString(32767);
        String TerminalBName = packetData.readString(32767);
        return new Combine(TerminalA, TerminalB, TerminalAName, TerminalBName, id);
    }
}