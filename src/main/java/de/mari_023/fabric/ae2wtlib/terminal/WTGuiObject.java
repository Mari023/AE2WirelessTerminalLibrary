package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.BiConsumer;

public abstract class WTGuiObject extends WirelessTerminalMenuHost implements IEnergySource, IActionHost {

    private final FixedViewCellInventory fixedViewCellInventory;
    private final PlayerEntity myPlayer;
    private boolean rangeCheck;

    public WTGuiObject(final PlayerEntity ep, int inventorySlot, final ItemStack is, BiConsumer<PlayerEntity, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        fixedViewCellInventory = new FixedViewCellInventory(is);
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

    public FixedViewCellInventory getViewCellStorage() {
        return fixedViewCellInventory;
    }

    @Override
    protected void setPowerDrainPerTick(double powerDrainPerTick) {
        if(rangeCheck) {
            super.setPowerDrainPerTick(powerDrainPerTick);
        } else {
            super.setPowerDrainPerTick(ae2wtlibConfig.INSTANCE.getOutOfRangePower());
        }
    }
}