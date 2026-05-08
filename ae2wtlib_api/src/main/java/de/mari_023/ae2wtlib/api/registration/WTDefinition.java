package de.mari_023.ae2wtlib.api.registration;

import java.util.*;
import java.util.function.BiConsumer;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import appeng.api.config.Actionable;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public record WTDefinition(String terminalName, ContainerOpener containerOpener, WTMenuHostFactory wTMenuHostFactory,
        MenuType<?> menuType, ItemWT item, @Nullable ItemStackTemplate universalTerminal,
        MutableComponent formattedName,
        String translationKey,
        String hotkeyName, DataComponentType<Unit> componentType, int upgradeCount, Icon icon) {
    @FunctionalInterface
    public interface ContainerOpener {
        boolean tryOpen(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu);
    }

    @FunctionalInterface
    public interface WTMenuHostFactory {
        WTMenuHost create(ItemWT item, Player player, ItemMenuHostLocator locator,
                BiConsumer<Player, ISubMenu> returnToMainMenu);
    }

    private static final Map<String, WTDefinition> wirelessTerminals = new HashMap<>();
    @ApiStatus.Internal
    public static final List<WTDefinition> wirelessTerminalList = new ArrayList<>();

    public static final Codec<WTDefinition> CODEC = Codec.STRING.comapFlatMap(s -> {
        var terminal = wirelessTerminals.get(s);
        if (terminal == null)
            return DataResult.error(() -> "WTDefinition " + s + " does not exist");
        return DataResult.success(terminal);
    }, WTDefinition::terminalName);
    public static final StreamCodec<ByteBuf, WTDefinition> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(WTDefinition::of, WTDefinition::terminalName);

    static void add(String terminalName, WTDefinition definition) {
        wirelessTerminals.put(terminalName, definition);
        wirelessTerminalList.add(definition);
    }

    public ItemStack universalTerminalStack() {
        if (universalTerminal() == null)
            return ItemStack.EMPTY;
        return universalTerminal().create();
    }

    public ItemStack universalTerminalStackWithEnergy() {
        ItemStack wut = universalTerminalStack();
        item().injectAEPower(wut, item().getAEMaxPower(wut), Actionable.MODULATE);
        return wut;
    }

    public static boolean exists(String terminalName) {
        return wirelessTerminals.containsKey(terminalName);
    }

    /**
     * Gets all terminal definitions.
     * 
     * @return a collection containing all terminal definitions
     */
    public static Collection<WTDefinition> wirelessTerminals() {
        return wirelessTerminals.values();
    }

    public static WTDefinition of(String name) {
        return Objects.requireNonNull(wirelessTerminals.get(name));
    }

    /**
     * Get the terminal definition of an ItemStack.
     * 
     * @param stack stack to get the terminal definition of.
     * @return the terminal definition, or throws a {@link NullPointerException} if none was found
     */
    public static WTDefinition of(ItemStack stack) {
        return Objects.requireNonNull(ofOrNull(stack));
    }

    /**
     * Get the terminal definition of an ItemStack.
     * 
     * @param stack stack to get the terminal definition of.
     * @return the terminal definition, or null if none was found
     */
    @Nullable
    public static WTDefinition ofOrNull(ItemStack stack) {
        return switch (stack.getItem()) {
            case ItemWUT _ -> {
                WTDefinition currentTerminal = stack.get(AE2wtlibComponents.CURRENT_TERMINAL);

                if (currentTerminal != null)
                    yield currentTerminal;
                for (var term : wirelessTerminals.entrySet())
                    if (stack.get(term.getValue().componentType()) != null) {
                        currentTerminal = term.getValue();
                        stack.set(AE2wtlibComponents.CURRENT_TERMINAL, currentTerminal);
                        yield currentTerminal;
                    }
                yield null;
            }
            case ItemWT item -> ofOrNull(item);
            default -> null;
        };
    }

    @Nullable
    private static WTDefinition ofOrNull(ItemWT item) {
        for (Map.Entry<String, WTDefinition> entry : wirelessTerminals.entrySet()) {
            if (item.equals(entry.getValue().item()))
                return entry.getValue();
        }
        return null;
    }
}
