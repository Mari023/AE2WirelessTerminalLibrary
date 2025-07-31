package de.mari_023.ae2wtlib.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AE2wtlibTags {
    public static TagKey<Item> NO_RESTOCK = itemTag("ae2wtlib:no_restock");

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
    }
}
