package de.mari_023.ae2wtlib.datagen;

import java.util.Objects;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessTerminalItem;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.TextConstants;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AE2wtlib.MOD_NAME, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation housing = AE2wtlib.id("item/common_terminal_housing");
        terminal(AEItems.WIRELESS_CRAFTING_TERMINAL.asItem(), housing, "crafting");
        terminal(AE2wtlibItems.PATTERN_ACCESS_TERMINAL, housing, "pattern_access");
        terminal(AE2wtlibItems.PATTERN_ENCODING_TERMINAL, housing, "pattern_encoding");
        terminal(AE2wtlibItems.UNIVERSAL_TERMINAL, housing, "universal");
        terminal(AEItems.WIRELESS_TERMINAL.asItem(), AE2wtlib.id("item/wireless_terminal_housing"), "");
    }

    private void terminal(WirelessTerminalItem item, ResourceLocation housing, String terminalName) {
        String registryNamePath = Objects.requireNonNull(item.getRegistryName()).getPath();

        ItemModelBuilder builder = terminal(registryNamePath, housing, terminalName, AEColor.TRANSPARENT, "lit");

        for (AEColor color : AEColor.values()) {
            var lit = terminal(registryNamePath + "_%s_%s", housing, terminalName, color, "lit");
            var unlit = terminal(registryNamePath + "_%s_%s", housing, terminalName, color, "unlit");

            builder = builder.override().predicate(TextConstants.COLOR, color.ordinal())
                    .predicate(TextConstants.LED_STATUS, 1)
                    .model(lit)
                    .end();
            builder = builder.override().predicate(TextConstants.COLOR, color.ordinal())
                    .predicate(TextConstants.LED_STATUS, 0)
                    .model(unlit)
                    .end();
        }
    }

    private ItemModelBuilder terminal(String path, ResourceLocation housing, String terminalName, AEColor color,
            String ledStatus) {
        if (!terminalName.isEmpty())
            terminalName = "_" + terminalName;
        String c = color.registryPrefix;
        return singleTexture(
                path.formatted(c, ledStatus),
                mcLoc("item/generated"),
                "layer0", housing)
                .texture("layer1", "item/wireless%s_terminal_%s".formatted(terminalName, c))
                .texture("layer2", "item/wireless_terminal_led_%s_%s".formatted(c, ledStatus));
    }
}
