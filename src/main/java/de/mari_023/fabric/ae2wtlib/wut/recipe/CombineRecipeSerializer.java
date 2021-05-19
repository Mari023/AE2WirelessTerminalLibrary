package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class CombineRecipeSerializer implements RecipeSerializer<Combine> {
    private CombineRecipeSerializer() { }

    public static final CombineRecipeSerializer INSTANCE = new CombineRecipeSerializer();

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

    private boolean validateOutput(String s) {
        if(s == null) return false;
        switch(s) {
            case "":
            case "name":
            case "currentTerminal":
            case "output":
            case "output0":
            case "output1":
            case "output2":
            case "trash":
            case "magnetCard":
            case "boosterCard":
            case "encryptionKey":
            case "substitute":
            case "craftingMode":
            case "internalMaxPower":
            case "crafting0":
            case "crafting1":
            case "crafting2":
            case "crafting3":
            case "crafting4":
            case "crafting5":
            case "crafting6":
            case "crafting7":
            case "crafting8":
            case "pattern0":
            case "pattern1":
            case "pattern_crafting0":
            case "pattern_crafting1":
            case "pattern_crafting2":
            case "pattern_crafting3":
            case "pattern_crafting4":
            case "pattern_crafting5":
            case "pattern_crafting6":
            case "pattern_crafting7":
            case "pattern_crafting8":
                return false;
            default:
                return true;
        }
    }
}