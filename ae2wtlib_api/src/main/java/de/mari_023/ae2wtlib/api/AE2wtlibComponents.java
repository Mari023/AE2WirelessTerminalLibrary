package de.mari_023.ae2wtlib.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import appeng.api.config.IncludeExclude;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.api.registration.WTDefinition;

public class AE2wtlibComponents {
    private static final Consumer<DataComponentType.Builder<CompoundTag>> COMPOUND_TAG_CODECS = builder -> builder
            .persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG);
    private static final Consumer<DataComponentType.Builder<IncludeExclude>> INCLUDE_EXCLUDE_CODECS = builder -> builder
            .persistent(Codec.BOOL.xmap(AE2wtlibComponents::booleanToIncludeExclude,
                    AE2wtlibComponents::includeExcludeToBoolean))
            .networkSynchronized(NeoForgeStreamCodecs.enumCodec(IncludeExclude.class));

    public static final StreamCodec<FriendlyByteBuf, ItemMenuHostLocator> MENU_HOST_LOCATOR_STREAM_CODEC = StreamCodec
            .ofMember((locator, buf) -> MenuLocators.writeToPacket(buf, locator),
                    (buf) -> (ItemMenuHostLocator) MenuLocators.readFromPacket(buf));

    public static final Map<ResourceLocation, DataComponentType<?>> DR = new HashMap<>();

    public static final DataComponentType<WTDefinition> CURRENT_TERMINAL = register("current_terminal",
            builder -> builder.persistent(WTDefinition.CODEC).networkSynchronized(WTDefinition.STREAM_CODEC));

    public static final DataComponentType<StackWrapper> SINGULARITY = register("singularity", builder -> builder
            .persistent(StackWrapper.CODEC).networkSynchronized(StackWrapper.STREAM_CODEC));
    public static final DataComponentType<ItemContainerContents> VIEW_CELL_INVENTORY = register("view_cell_inv",
            builder -> builder.persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DataComponentType<CompoundTag> PICKUP_CONFIG = register("pickup_config", COMPOUND_TAG_CODECS);
    public static final DataComponentType<CompoundTag> INSERT_CONFIG = register("insert_config", COMPOUND_TAG_CODECS);
    public static final DataComponentType<IncludeExclude> PICKUP_MODE = register("pickup_mode", INCLUDE_EXCLUDE_CODECS);
    public static final DataComponentType<IncludeExclude> INSERT_MODE = register("insert_mode", INCLUDE_EXCLUDE_CODECS);
    public static final DataComponentType<Boolean> RESTOCK = register("restock", builder -> builder
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> PICK_BLOCK = register("pick_block", builder -> builder
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> CRAFT_IF_MISSING = register("craft_if_missing", builder -> builder
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<CompoundTag> PATTERN_ENCODING_LOGIC = register("pattern_encoding_logic",
            COMPOUND_TAG_CODECS);

    public static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.put(AE2wtlibAPI.id(name), componentType);
        return componentType;
    }

    private static boolean includeExcludeToBoolean(IncludeExclude includeExclude) {
        return switch (includeExclude) {
            case WHITELIST -> true;
            case BLACKLIST -> false;
        };
    }

    private static IncludeExclude booleanToIncludeExclude(boolean b) {
        return b ? IncludeExclude.WHITELIST : IncludeExclude.BLACKLIST;
    }
}
