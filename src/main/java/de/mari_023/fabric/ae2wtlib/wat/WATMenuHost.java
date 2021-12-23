package de.mari_023.fabric.ae2wtlib.wat;

import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WATMenuHost extends WTMenuHost {

    public WATMenuHost(final Player ep, @Nullable Integer inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
    }

    @Override
    public MenuType<?> getType() {
        return WATMenu.TYPE;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AE2wtlib.PATTERN_ACCESS_TERMINAL);
    }
}