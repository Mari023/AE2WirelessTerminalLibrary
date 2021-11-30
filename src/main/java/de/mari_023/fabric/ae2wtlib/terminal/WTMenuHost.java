package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.BiConsumer;

public abstract class WTMenuHost extends WirelessTerminalMenuHost implements IEnergySource, IActionHost {

    private final ViewCellInventory viewCellInventory;
    private final PlayerEntity myPlayer;
    private boolean rangeCheck;

    public WTMenuHost(final PlayerEntity ep, int inventorySlot, final ItemStack is, BiConsumer<PlayerEntity, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        viewCellInventory = new ViewCellInventory(is);
        myPlayer = ep;
    }

    public abstract ScreenHandlerType<?> getType();

    public boolean rangeCheck() {
        rangeCheck = super.rangeCheck();
        return rangeCheck || hasBoosterCard();
    }

    public boolean hasBoosterCard() {
        return ((IInfinityBoosterCardHolder) getItemStack().getItem()).hasBoosterCard(getItemStack());
    }

    public PlayerEntity getPlayer() {
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