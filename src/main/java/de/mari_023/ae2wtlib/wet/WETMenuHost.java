package de.mari_023.ae2wtlib.wet;

import java.util.function.BiConsumer;

import de.mari_023.ae2wtlib.ValueIOHelper;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.parts.encoding.PatternEncodingLogic;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.world.level.storage.TagValueOutput;

public class WETMenuHost extends WTMenuHost
        implements IViewCellStorage, IPatternTerminalMenuHost, IPatternTerminalLogicHost {
    private final PatternEncodingLogic logic = new PatternEncodingLogic(this);

    public WETMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        logic.readFromNBT(ValueIOHelper.fromComponent(getPlayer(), getItemStack(), AE2wtlibComponents.PATTERN_ENCODING_LOGIC));
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
        TagValueOutput tagValueOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        logic.writeToNBT(tagValueOutput);
        getItemStack().set(AE2wtlibComponents.PATTERN_ENCODING_LOGIC, tagValueOutput.buildResult());
    }
}
