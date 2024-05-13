/*
 * Copied from Modern Industrialisation (https://github.com/AztechMC/Modern-Industrialization/blob/d537c128c9af90699ff55942da8b6da159222c80/src/client/java/aztech/modern_industrialization/datagen/texture/TexturesProvider.java) and adapted for ae2wtlib
 */
/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.mari_023.ae2wtlib.datagen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import de.mari_023.ae2wtlib.AE2wtlib;

public record TexturesProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) implements DataProvider {
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var packs = new ArrayList<PackResources>();

        packs.add(new VanillaPackResourcesBuilder().exposeNamespace("minecraft").pushJarResources()
                .build(new PackLocationInfo("vanilla-textures", Component.literal("vanilla-textures"),
                        PackSource.BUILT_IN, Optional.empty())));
        packs.add(new FastPathPackResources("nonGen", packOutput.getOutputFolder().resolve("../../main/resources")));

        var fallbackProvider = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs);
        var outputPack = new MultiPackResourceManager(PackType.CLIENT_RESOURCES,
                List.of(new FastPathPackResources("gen", packOutput.getOutputFolder())));

        return AE2wtlibTextures.offerTextures(
                (image, textureId) -> writeTexture(cache, image, textureId),
                resourceLocation -> {
                    // Generated first
                    var generated = outputPack.getResource(resourceLocation);
                    if (generated.isPresent()) {
                        return generated;
                    }
                    return fallbackProvider.getResource(resourceLocation);
                },
                existingFileHelper)
                .whenComplete((result, throwable) -> outputPack.close());
    }

    private void writeTexture(CachedOutput cache, NativeImage image, String textureId) {
        try {
            var path = packOutput.getOutputFolder().resolve("assets").resolve(textureId.replace(':', '/'));
            cache.writeIfNeeded(path, image.asByteArray(), Hashing.sha256().hashBytes(image.asByteArray()));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write texture " + textureId, ex);
        }
    }

    @Override
    public String getName() {
        return "Textures: " + AE2wtlib.MOD_NAME;
    }
}
