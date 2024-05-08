package de.mari_023.ae2wtlib.datagen;

import java.util.Objects;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEItems;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AE2wtlib.MOD_NAME, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        terminal((ItemWT) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem(), "crafting");
        terminal(AE2wtlibItems.PATTERN_ACCESS_TERMINAL, "pattern_access");
        terminal(AE2wtlibItems.PATTERN_ENCODING_TERMINAL, "pattern_encoding");
        terminal(AE2wtlibItems.UNIVERSAL_TERMINAL, "universal");
    }

    private void terminal(ItemWT item, String terminalName) {
        String registryNamePath = Objects.requireNonNull(item.getRegistryName()).getPath();
        String c = AEColor.TRANSPARENT.registryPrefix;
        singleTexture(
                registryNamePath,
                mcLoc("item/generated"),
                "layer0", AE2wtlib.id("item/terminal_housing"))
                .texture("layer1", "item/wireless_%s_terminal_background_%s".formatted(terminalName, c))
                .texture("layer2", "item/wireless_%s_terminal_foreground_%s".formatted(terminalName, c))
                .texture("layer3", "item/wireless_terminal_led_%s".formatted(c));
        // TODO overrides for each color

        for (AEColor color : AEColor.values()) {
            terminal(registryNamePath + "_%s", terminalName, color);
        }
    }

    private void terminal(String path, String terminalName, AEColor color) {
        String c = color.registryPrefix;
        singleTexture(
                path.formatted(c),
                mcLoc("item/generated"),
                "layer0", AE2wtlib.id("item/terminal_housing"))
                .texture("layer1", "item/wireless_%s_terminal_background_%s".formatted(terminalName, c))
                .texture("layer2", "item/wireless_%s_terminal_foreground_%s".formatted(terminalName, c))
                .texture("layer3", "item/wireless_terminal_led_%s".formatted(c));
    }
}
