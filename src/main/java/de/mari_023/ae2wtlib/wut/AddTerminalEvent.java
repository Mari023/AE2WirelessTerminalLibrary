package de.mari_023.ae2wtlib.wut;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.terminal.ItemWT;

public final class AddTerminalEvent {
    private AddTerminalEvent() {
    }

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

    public static synchronized boolean hasRun() {
        return HANDLERS == null;
    }

    /**
     * Creates a builder to register a new terminal.
     *
     * @param terminalName      Terminal's name.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     */
    public WTDefinitionBuilder builder(String terminalName, WTDefinition.WTMenuHostFactory WTMenuHostFactory,
            MenuType<?> menuType, ItemWT item) {
        return new WTDefinitionBuilder(this, terminalName, WTMenuHostFactory, menuType, item);
    }
}
