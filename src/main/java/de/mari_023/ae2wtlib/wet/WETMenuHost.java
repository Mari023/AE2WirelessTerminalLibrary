package de.mari_023.ae2wtlib.wet;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.parts.encoding.PatternEncodingLogic;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class WETMenuHost extends WTMenuHost
        implements IViewCellStorage, IPatternTerminalMenuHost, IPatternTerminalLogicHost {
    private final PatternEncodingLogic logic = new PatternEncodingLogic(this);

    public WETMenuHost(WirelessTerminalItem item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        readFromNbt();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlibItems.instance().PATTERN_ENCODING_TERMINAL);
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();
        logic.readFromNBT(getItemStack().getOrCreateTag());
    }

    @Override
    public void saveChanges() {
        super.saveChanges();
        logic.writeToNBT(getItemStack().getOrCreateTag());
    }

    @Override
    public PatternEncodingLogic getLogic() {
        return logic;
    }

    @Override
    public Level getLevel() {
        return getPlayer().level();
    }

    @Override
    public void markForSave() {
        saveChanges();
    }
}
