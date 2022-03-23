package de.mari_023.ae2wtlib.forge.curio;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import appeng.menu.locator.MenuLocator;

public final class CurioHelper {
    private CurioHelper() {
    }

    public static boolean isStillPresent(Player player, ItemStack terminal) {
        List<SlotResult> slotResults = CuriosApi.getCuriosHelper().findCurios(player, terminal.getItem());
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().equals(terminal)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getItemStack(Player player, MenuLocator locator) {
        if (locator instanceof CurioLocator curioLocator)
            return curioLocator.locateItem(player);
        return ItemStack.EMPTY;
    }

    @Nullable
    public static MenuLocator findTerminal(Player player, String terminalName) {
        List<SlotResult> slotResults = CuriosApi.getCuriosHelper().findCurios(player, AE2wtlib.UNIVERSAL_TERMINAL);
        for (SlotResult slotResult : slotResults) {
            if (WUTHandler.hasTerminal(slotResult.stack(), terminalName)) {
                return new CurioLocator(slotResult.slotContext().identifier(), slotResult.slotContext().index());
            }
        }

        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, (Item) WUTHandler.wirelessTerminals.get(terminalName).item())
                .map(result -> new CurioLocator(result.slotContext().identifier(), result.slotContext().index()))
                .orElse(null);
    }
}
