package de.mari_023.fabric.ae2wtlib.terminal;

import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class ItemInfinityBooster extends Item {
    public ItemInfinityBooster() {
        super(new FabricItemSettings().group(ae2wtlib.ITEM_GROUP).maxCount(1));
    }
}