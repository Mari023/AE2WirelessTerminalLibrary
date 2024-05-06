package de.mari_023.ae2wtlib.wut;


import appeng.api.config.Actionable;
import appeng.hotkeys.HotkeyActions;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import de.mari_023.ae2wtlib.AE2wtlibComponents;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.hotkeys.Ae2wtlibLocatingService;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class AddTerminalEvent {
    private AddTerminalEvent() {}
    @Nullable
    private static List<Consumer<AddTerminalEvent>> HANDLERS = new ArrayList<>();

    public static synchronized void register(Consumer<AddTerminalEvent> handler) {
        if (HANDLERS == null)
            throw new IllegalStateException(
                    "Cannot register terminal registration handler after terminal registration already happened");
        HANDLERS.add(handler);
    }

    public static synchronized void run() {
        if (HANDLERS == null)
            throw new IllegalStateException("Cannot run terminal registration handler twice");
        var event = new AddTerminalEvent();
        HANDLERS.forEach(c -> c.accept(event));
        HANDLERS = null;
    }

    /**
     * Registers a new terminal.
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     * @param hotkeyName        The hotkey name for the terminal.
     * @param itemID            The item ID for the terminal.
     */
    public synchronized void addTerminal(String name, WTDefinition.ContainerOpener open,
                                         WTDefinition.WTMenuHostFactory WTMenuHostFactory,
                                         MenuType<?> menuType, ItemWT item, String hotkeyName, String itemID) {
        if (WUTHandler.terminalNames.contains(name))
            return;

        ItemStack wut = new ItemStack(AE2wtlibItems.instance().UNIVERSAL_TERMINAL);
        DataComponentType<Unit> component = AE2wtlibComponents.register("has_" + name + "_terminal",
                builder -> builder
                        .persistent(Codec.EMPTY.codec())
                        .networkSynchronized(NeoForgeStreamCodecs.enumCodec(Unit.class)));
        wut.set(component, Unit.INSTANCE);
        AE2wtlibItems.instance().UNIVERSAL_TERMINAL.injectAEPower(wut,
                AE2wtlibItems.instance().UNIVERSAL_TERMINAL.getAEMaxPower(wut), Actionable.MODULATE);

        HotkeyActions.register(new Ae2wtlibLocatingService(name), hotkeyName);

        WUTHandler.wirelessTerminals.put(name, new WTDefinition(open, WTMenuHostFactory, menuType, item, wut,
                TextConstants.formatTerminalName(itemID), hotkeyName, component));
        WUTHandler.terminalNames.add(name);
    }

    /**
     * Registers a new terminal. Helper for terminals which follow the "wireless_" + name + "_terminal" scheme for
     * hotkeyName
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     * @param itemID            The item ID for the terminal.
     */
    public void addTerminal(String name, WTDefinition.ContainerOpener open,
                            WTDefinition.WTMenuHostFactory WTMenuHostFactory,
                            MenuType<?> menuType, ItemWT item, String itemID) {
        addTerminal(name, open, WTMenuHostFactory, menuType, item, "wireless_" + name + "_terminal", itemID);
    }

    /**
     * Registers a new terminal with the handler. Helper for terminals which follow the "wireless_" + name +
     * "_terminal" scheme for hotkeyName and the "item.ae2wtlib.wireless_" + name + "_terminal" scheme for the
     * itemID
     *
     * @param name              Terminal's name.
     * @param open              Container opener for the terminal.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     */
    public void addTerminal(String name, WTDefinition.ContainerOpener open,
                            WTDefinition.WTMenuHostFactory WTMenuHostFactory,
                            MenuType<?> menuType, ItemWT item) {
        addTerminal(name, open, WTMenuHostFactory, menuType, item, "item.ae2wtlib.wireless_" + name + "_terminal");
    }
}
