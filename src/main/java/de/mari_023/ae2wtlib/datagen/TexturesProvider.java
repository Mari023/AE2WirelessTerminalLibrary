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
            cache.writeIfNeeded(path, image.asByteArray(), Hashing.sha1().hashBytes(image.asByteArray()));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write texture " + textureId, ex);
        }
    }

    @Override
    public String getName() {
        return "Textures";
    }
}
