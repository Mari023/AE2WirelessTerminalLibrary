package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class WTMenuHost extends WirelessTerminalMenuHost {

    private final ViewCellInventory viewCellInventory;
    private final Player myPlayer;
    private boolean rangeCheck;

    public WTMenuHost(final Player ep, int inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        viewCellInventory = new ViewCellInventory(is);
        myPlayer = ep;
    }

    public abstract MenuType<?> getType();

    public boolean rangeCheck() {
        rangeCheck = super.rangeCheck();
        return rangeCheck || hasBoosterCard();
    }

    public boolean hasBoosterCard() {
        return ((IInfinityBoosterCardHolder) getItemStack().getItem()).hasBoosterCard(getItemStack());
    }

    public Player getPlayer() {
        return myPlayer;
    }

    public ViewCellInventory getViewCellStorage() {
        return viewCellInventory;
    }

    @Override
    protected void setPowerDrainPerTick(double powerDrainPerTick) {
        if(rangeCheck) {
            super.setPowerDrainPerTick(powerDrainPerTick);
        } else {
            super.setPowerDrainPerTick(AE2wtlibConfig.INSTANCE.getOutOfRangePower());
        }
    }
}