package de.mari_023.fabric.ae2wtlib.wut.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class UpgradeSerializer extends Serializer<Upgrade> {
    public static final UpgradeSerializer INSTANCE = new UpgradeSerializer();
    public static final String NAME = "upgrade";
    public static final Identifier ID = new Identifier(AE2wtlib.MOD_NAME, NAME);

    @Override
    public Upgrade read(Identifier id, JsonObject json) {
        UpgradeJsonFormat recipeJson = new Gson().fromJson(json, UpgradeJsonFormat.class);
        if(recipeJson.terminal == null || validateOutput(recipeJson.terminalName))
            throw new JsonSyntaxException("A required attribute is missing or invalid!");

        return new Upgrade(Ingredient.fromJson(recipeJson.terminal), recipeJson.terminalName, id);
    }

    @Override
    public void write(PacketByteBuf packetData, Upgrade recipe) {
        recipe.getTerminal().write(packetData);
        packetData.writeString(recipe.getTerminalName());
    }

    @Override
    public Upgrade read(Identifier id, PacketByteBuf packetData) {
        return new Upgrade(Ingredient.fromPacket(packetData), packetData.readString(32767), id);
    }
}