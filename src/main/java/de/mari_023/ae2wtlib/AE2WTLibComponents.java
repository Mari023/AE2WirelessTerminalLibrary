package de.mari_023.ae2wtlib;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.config.IncludeExclude;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;

public class AE2WTLibComponents {
    private static final Consumer<DataComponentType.Builder<CompoundTag>> COMPOUND_TAG_CODECS = builder -> builder
            .persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG);
    private static final Consumer<DataComponentType.Builder<IncludeExclude>> INCLUDE_EXCLUDE_CODECS = builder -> builder
            .persistent(
                    RecordCodecBuilder.<IncludeExclude>mapCodec(
                            codecBuilder -> codecBuilder
                                    .group(Codec.BOOL.fieldOf("")
                                            .forGetter(AE2WTLibComponents::includeExcludeToBoolean))
                                    .apply(codecBuilder, AE2WTLibComponents::booleanToIncludeExclude))
                            .codec())
            .networkSynchronized(StreamCodec.composite(
                    ByteBufCodecs.BOOL, AE2WTLibComponents::includeExcludeToBoolean,
                    AE2WTLibComponents::booleanToIncludeExclude));

    public static final StreamCodec<FriendlyByteBuf, ItemMenuHostLocator> MENU_HOST_LOCATOR_STREAM_CODEC = StreamCodec
            .ofMember(
                    (locator, buf) -> MenuLocators.writeToPacket(buf, locator),
                    (buf) -> (ItemMenuHostLocator) MenuLocators.readFromPacket(buf));

    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, AE2wtlib.MOD_NAME);

    public static final DataComponentType<String> CURRENT_TERMINAL = register("currentTerminal",
            builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DataComponentType<List<String>> INSTALLED_TERMINALS = register("installedTerminals",
            builder -> builder.persistent(Codec.STRING.listOf())
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())));

    public static final DataComponentType<ItemStack> SINGULARITY = register("singularity", builder -> builder
            .persistent(ItemStack.OPTIONAL_CODEC).networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC));
    public static final DataComponentType<ItemContainerContents> VIEW_CELL_INVENTORY = register("view_cell_inv",
            builder -> builder.persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DataComponentType<MagnetMode> MAGNET_SETTINGS = register("magnetSettings",
            builder -> builder.persistent(MagnetMode.CODEC).networkSynchronized(MagnetMode.STREAM_CODEC));
    public static final DataComponentType<CompoundTag> PICKUP_CONFIG = register("pickupConfig", COMPOUND_TAG_CODECS);
    public static final DataComponentType<CompoundTag> INSERT_CONFIG = register("insertConfig", COMPOUND_TAG_CODECS);
    public static final DataComponentType<IncludeExclude> PICKUP_MODE = register("pickupMode", INCLUDE_EXCLUDE_CODECS);
    public static final DataComponentType<IncludeExclude> INSERT_MODE = register("insertMode", INCLUDE_EXCLUDE_CODECS);
    public static final DataComponentType<Boolean> RESTOCK = register("restock", builder -> builder
            .persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<CompoundTag> PATTERN_ENCODING_LOGIC = register("PatternEncodingLogic",
            COMPOUND_TAG_CODECS);

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
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
