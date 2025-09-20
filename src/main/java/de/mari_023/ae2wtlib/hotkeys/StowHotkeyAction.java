package de.mari_023.ae2wtlib.hotkeys;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import appeng.api.config.Actionable;
import appeng.api.features.HotkeyAction;
import appeng.api.stacks.AEItemKey;
import appeng.me.helpers.PlayerSource;

import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

public class StowHotkeyAction implements HotkeyAction {
    @Override
    public boolean run(Player player) {
        var handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        if (!handler.inRange())
            return false;
        var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty())
            return false;
        if (handler.getTargetGrid() == null)
            return false;
        if (stack.isNotReplaceableByPickAction(player, player.getInventory().getSelectedSlot()))
            return false;

        stack.setCount(stack.getCount() - (int) handler.getTargetGrid().getStorageService().getInventory()
                .insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, new PlayerSource(player)));
        return true;
    }
}
