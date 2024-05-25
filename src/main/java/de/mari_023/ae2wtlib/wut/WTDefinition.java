package de.mari_023.ae2wtlib.wut;

import java.util.*;
import java.util.function.BiConsumer;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.AE2wtlibComponents;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public record WTDefinition(String terminalName, ContainerOpener containerOpener, WTMenuHostFactory wTMenuHostFactory,
        MenuType<?> menuType, ItemWT item, ItemStack universalTerminal, MutableComponent formattedName,
        String hotkeyName, DataComponentType<Unit> componentType, int upgradeCount) {
    @FunctionalInterface
    public interface ContainerOpener {
        boolean tryOpen(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu);
    }

    @FunctionalInterface
    public interface WTMenuHostFactory {
        WTMenuHost create(ItemWT item, Player player, ItemMenuHostLocator locator,
                BiConsumer<Player, ISubMenu> returnToMainMenu);
    }

    public static final Codec<WTDefinition> CODEC = RecordCodecBuilder.<WTDefinition>mapCodec(
            builder -> builder.group(Codec.STRING.fieldOf("").forGetter(WTDefinition::terminalName))
                    .apply(builder, WTDefinition::of))
            .codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, WTDefinition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, WTDefinition::terminalName,
            WTDefinition::of);

    private static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();

    static Map<String, WTDefinition> map() {
        return wirelessTerminals;
    }

    public static Collection<WTDefinition> wirelessTerminals() {
        return wirelessTerminals.values();
    }

    public static List<String> terminalNames() {
        return List.copyOf(wirelessTerminals.keySet());
    }

    public static WTDefinition of(String name) {
        return Objects.requireNonNull(ofOrNull(name));
    }

    @Nullable
    public static WTDefinition ofOrNull(String name) {
        return wirelessTerminals.get(name);
    }

    public static WTDefinition of(ItemStack stack) {
        return Objects.requireNonNull(ofOrNull(stack));
    }

    @Nullable
    public static WTDefinition ofOrNull(ItemStack stack) {
        return switch (stack.getItem()) {
            case ItemWUT ignored -> {
                WTDefinition currentTerminal = stack.get(AE2wtlibComponents.CURRENT_TERMINAL);

                if (currentTerminal != null)
                    yield currentTerminal;
                for (var term : WTDefinition.wirelessTerminals.entrySet())
                    if (stack.get(term.getValue().componentType()) != null) {
                        currentTerminal = term.getValue();
                        stack.set(AE2wtlibComponents.CURRENT_TERMINAL, currentTerminal);
                        yield currentTerminal;
                    }
                yield null;
            }
            case ItemWT item -> of(item);
            default -> null;
        };
    }

    public static WTDefinition of(ItemWT item) {
        return Objects.requireNonNull(ofOrNull(item));
    }

    @Nullable
    public static WTDefinition ofOrNull(ItemWT item) {
        for (Map.Entry<String, WTDefinition> entry : WTDefinition.wirelessTerminals.entrySet()) {
            if (item.equals(entry.getValue().item()))
                return entry.getValue();
        }
        return null;
    }
}
