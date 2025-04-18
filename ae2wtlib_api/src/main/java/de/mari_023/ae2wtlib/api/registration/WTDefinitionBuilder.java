package de.mari_023.ae2wtlib.api.registration;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import appeng.hotkeys.HotkeyActions;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.terminal.Ae2wtlibLocatingService;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;

public class WTDefinitionBuilder {
    private final AddTerminalEvent event;
    private final String name;
    private final WTDefinition.WTMenuHostFactory wTMenuHostFactory;
    private final MenuType<?> menuType;
    private final ItemWT item;
    private final Icon icon;
    private String hotkeyName;
    @Nullable
    private DataComponentType<Unit> componentType;
    private String translationKey;
    private int upgradeCount;

    WTDefinitionBuilder(AddTerminalEvent event, String terminalName,
            WTDefinition.WTMenuHostFactory wTMenuHostFactory, MenuType<?> menuType, ItemWT item, Icon icon) {
        this.event = event;
        this.name = terminalName;
        this.wTMenuHostFactory = wTMenuHostFactory;
        this.menuType = menuType;
        this.item = item;

        this.hotkeyName = "wireless_" + terminalName + "_terminal";
        this.translationKey = item.getDescriptionId();
        this.upgradeCount = 2;
        this.icon = icon;
    }

    /**
     * Add the terminal.
     */
    public void addTerminal() {
        if (componentType == null) {
            componentType = AE2wtlibComponents.register("has_" + name + "_terminal", builder -> builder
                    .persistent(Codec.EMPTY.codec())
                    .networkSynchronized(NeoForgeStreamCodecs.enumCodec(Unit.class)));
        }

        if (WTDefinition.exists(name))
            throw new IllegalStateException("Trying to register terminal with name " + name + " but it already exists");

        var wut = AE2wtlibAPI.makeWUT(componentType);

        WTDefinition wtDefinition = new WTDefinition(name, item::tryOpen, wTMenuHostFactory, menuType, item, wut,
                TextConstants.formatTerminalName(translationKey), translationKey, hotkeyName, componentType,
                upgradeCount, icon);

        HotkeyActions.register(new Ae2wtlibLocatingService(wtDefinition), hotkeyName);

        synchronized (event) {
            WTDefinition.add(name, wtDefinition);
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
     * Since 19.2.3, the translationKey is set automatically from the item.
     *
     * @param translationKey The translationKey for the terminal.
     * @return this
     */
    @Deprecated(forRemoval = true, since = "19.2.3")
    @Contract("_ -> this")
    public WTDefinitionBuilder translationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    /**
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
