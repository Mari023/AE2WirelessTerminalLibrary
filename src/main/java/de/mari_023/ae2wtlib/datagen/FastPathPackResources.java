package de.mari_023.ae2wtlib.datagen;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;

/**
 * A PathPackResources that uses {@link File#exists()} instead of {@link Files#exists} with the default FS.
 */
public class FastPathPackResources extends PathPackResources {
    private final Path root;

    public FastPathPackResources(String name, Path root) {
        super(new PackLocationInfo(name, Component.empty(), PackSource.BUILT_IN, Optional.empty()), root);
        this.root = root;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        Path path = this.root.resolve(packType.getDirectory()).resolve(location.getNamespace());
        return getResource(location, path);
    }

    public static IoSupplier<InputStream> getResource(ResourceLocation location, Path path) {
        return FileUtil.decomposePath(location.getPath()).mapOrElse(list -> {
            Path path2 = FileUtil.resolvePath(path, list);
            return returnFileIfExists(path2);
        }, partialResult -> null);
    }

    @Nullable
    private static IoSupplier<InputStream> returnFileIfExists(Path path) {
        return exists(path) ? IoSupplier.create(path) : null;
    }

    private static final FileSystem DEFAULT_FS = FileSystems.getDefault();

    private static boolean exists(Path path) {
        // NIO Files.exists is notoriously slow when checking the file system
        return path.getFileSystem() == DEFAULT_FS ? path.toFile().exists() : Files.exists(path);
    }
}
