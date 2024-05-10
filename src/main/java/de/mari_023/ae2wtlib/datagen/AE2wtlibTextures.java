package de.mari_023.ae2wtlib.datagen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.Util;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import appeng.api.util.AEColor;

public final class AE2wtlibTextures {
    private AE2wtlibTextures() {
    }

    public static CompletableFuture<?> offerTextures(BiConsumer<NativeImage, String> textureWriter,
            ResourceProvider manager, ExistingFileHelper fileHelper) {
        TextureManager mtm = new TextureManager(manager, textureWriter);

        // Texture generation runs in two phases:
        // 1. all textures that don't depend on other generated textures are submitted to {@code defer.accept} and
        // generated in parallel.
        // 2. textures that depend on other generated textures are submitted to {@code mtm.runAtEnd} are generated in
        // parallel once phase 1. is
        // complete.

        // Futures for the first work phase
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Consumer<IORunnable> defer = r -> futures
                .add(CompletableFuture.runAsync(r::safeRun, Util.backgroundExecutor()));

        for (AEColor color : AEColor.values()) {
            var colorAmp = new ColorMap(color);
            defer.accept(() -> generateTerminalTexture(mtm, "", color, colorAmp));
            defer.accept(() -> generateTerminalTexture(mtm, "universal", color, colorAmp));
            defer.accept(() -> generateTerminalTexture(mtm, "crafting", color, colorAmp));
            defer.accept(() -> generateTerminalTexture(mtm, "pattern_access", color, colorAmp));
            defer.accept(() -> generateTerminalTexture(mtm, "pattern_encoding", color, colorAmp));

            defer.accept(() -> generateLEDTexture(mtm, "lit", color, colorAmp));
            defer.accept(() -> generateLEDTexture(mtm, "unlit", color, colorAmp));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenComposeAsync(v -> {
                    // Do second phase work
                    return mtm.doEndWork();
                }, Util.backgroundExecutor())
                .thenRun(() -> mtm.markTexturesAsGenerated(fileHelper));
    }

    private static String getTemplate(String name) {
        return String.format("ae2wtlib:textures/item/wireless_terminal_base/%s.png", name);
    }

    public static void generateTerminalTexture(TextureManager mtm, String terminal, AEColor color, ColorMap colorMap) {
        try {
            NativeImage texture = generateTexture(mtm, terminal, colorMap);

            if (!terminal.isEmpty())
                terminal = "_" + terminal;
            appendTexture(mtm, texture, "wireless%s_terminal_%s".formatted(terminal, color.registryPrefix));
            texture.close();
        } catch (Throwable ignored) {
        }
    }

    public static void generateLEDTexture(TextureManager mtm, String status, AEColor color, ColorMap colorMap) {
        try {
            NativeImage texture = generateTexture(mtm, "led_" + status, colorMap);

            appendTexture(mtm, texture, "wireless_terminal_led_%s_%s".formatted(color.registryPrefix, status));
            texture.close();
        } catch (Throwable ignored) {
        }
    }

    public static NativeImage generateTexture(TextureManager mtm, String name, ColorMap colorMap) throws IOException {
        NativeImage image = null;
        String template = getTemplate(name);

        if (mtm.hasAsset(template)) {
            image = mtm.getAssetAsTexture(template);
            colorize(image, colorMap);
        }
        if (image == null)
            throw new RuntimeException("Failed to generate texture for " + name);
        return image;
    }

    public static void colorize(NativeImage image, ColorMap colorramp) {
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                image.setPixelRGBA(i, j, colorramp.map(image.getPixelRGBA(i, j)));
            }
        }
    }

    public static void appendTexture(TextureManager mtm, NativeImage texture, String path)
            throws IOException {
        String texturePath = String.format("ae2wtlib:textures/item/%s.png", path);
        mtm.addTexture(texturePath, texture);
        texture.close();
    }
}
