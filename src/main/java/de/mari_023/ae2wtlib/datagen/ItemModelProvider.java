package de.mari_023.ae2wtlib.datagen;

import java.util.Objects;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEItems;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    private static final ResourceLocation COLOR = AE2wtlib.id("color");

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
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
        String registryNameNamespace = registryName.getNamespace();
        String registryNamePath = registryName.getPath();

        ItemModelBuilder builder = terminal(registryNamePath, terminalName, AEColor.TRANSPARENT);

        for (AEColor color : AEColor.values()) {
            terminal(registryNamePath + "_%s", terminalName, color);

            builder = builder.override().predicate(COLOR, color.ordinal()).model(new ModelFile.ExistingModelFile(
                    new ResourceLocation(registryNameNamespace, registryNamePath + "_" + color.registryPrefix),
                    existingFileHelper)).end();
        }
    }

    private ItemModelBuilder terminal(String path, String terminalName, AEColor color) {
        String c = color.registryPrefix;
        return singleTexture(
                path.formatted(c),
                mcLoc("item/generated"),
                "layer0", AE2wtlib.id("item/terminal_housing"))
                .texture("layer1", "item/wireless_%s_terminal_%s".formatted(terminalName, c))
                .texture("layer2", "item/wireless_terminal_led_%s".formatted(c));
    }
}
