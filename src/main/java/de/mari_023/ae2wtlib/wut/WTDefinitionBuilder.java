package de.mari_023.ae2wtlib.wut;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import appeng.api.config.Actionable;
import appeng.hotkeys.HotkeyActions;

import de.mari_023.ae2wtlib.AE2wtlibComponents;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.hotkeys.Ae2wtlibLocatingService;
import de.mari_023.ae2wtlib.terminal.ItemWT;

public class WTDefinitionBuilder {
    private final AddTerminalEvent event;
    private final String name;
    private final WTDefinition.WTMenuHostFactory wTMenuHostFactory;
    private final MenuType<?> menuType;
    private final ItemWT item;
    private String hotkeyName;
    @Nullable
    private DataComponentType<Unit> componentType;
    private String translationKey;
    private int upgradeCount;

    /**
     * internal, use {@link AddTerminalEvent#builder} Creates a builder to register a new terminal.
     *
     * @param terminalName      Terminal's name.
     * @param WTMenuHostFactory The factory for creating WTMenuHost.
     * @param menuType          The menu type for the terminal.
     * @param item              The item representing the terminal.
     */
    protected WTDefinitionBuilder(AddTerminalEvent event, String terminalName,
            WTDefinition.WTMenuHostFactory WTMenuHostFactory, MenuType<?> menuType, ItemWT item) {
        this.event = event;
        this.name = terminalName;
        this.wTMenuHostFactory = WTMenuHostFactory;
        this.menuType = menuType;
        this.item = item;

        this.hotkeyName = "wireless_" + terminalName + "_terminal";
        this.translationKey = "item.ae2wtlib.wireless_" + terminalName + "_terminal";
        this.upgradeCount = 2;
    }

    /**
     * add the terminal.
     */
    public void addTerminal() {
        if (componentType == null) {
            componentType = AE2wtlibComponents.register("has_" + name + "_terminal", builder -> builder
                    .persistent(Codec.EMPTY.codec())
                    .networkSynchronized(NeoForgeStreamCodecs.enumCodec(Unit.class)));
        }

        if (WTDefinition.map().containsKey(name))
            throw new IllegalStateException();

        ItemStack wut = new ItemStack(AE2wtlibItems.UNIVERSAL_TERMINAL);

        wut.set(componentType, Unit.INSTANCE);
        AE2wtlibItems.UNIVERSAL_TERMINAL.injectAEPower(wut,
                AE2wtlibItems.UNIVERSAL_TERMINAL.getAEMaxPower(wut), Actionable.MODULATE);

        WTDefinition wtDefinition = new WTDefinition(name, item::tryOpen, wTMenuHostFactory, menuType, item, wut,
                TextConstants.formatTerminalName(translationKey), hotkeyName, componentType, upgradeCount);

        HotkeyActions.register(new Ae2wtlibLocatingService(wtDefinition), hotkeyName);

        synchronized (event) {
            WTDefinition.map().put(name, wtDefinition);
        }
    }

    /**
     * override the hotkey name instead of generating one based on the terminalName
     *
     * @param hotkeyName The hotkey name for the terminal.
     * @return this
     */
    @Contract("_ -> this")
    public WTDefinitionBuilder hotkeyName(String hotkeyName) {
        this.hotkeyName = hotkeyName;
        return this;
    }

    /**
     *
     * @param translationKey The translationKey for the terminal.
     * @return this
     */
    @Contract("_ -> this")
    public WTDefinitionBuilder translationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    /**
     *
     * @param upgradeCount How many upgrades does this support.
     * @return this
     */
    @Contract("_ -> this")
    public WTDefinitionBuilder upgradeCount(int upgradeCount) {
        this.upgradeCount = upgradeCount;
        return this;
    }

    /**
     * Equivalent to {@link WTDefinitionBuilder#upgradeCount (0)}
     *
     * @return this
     */
    @Contract(" -> this")
    public WTDefinitionBuilder noUpgrades() {
        return upgradeCount(0);
    }
}
