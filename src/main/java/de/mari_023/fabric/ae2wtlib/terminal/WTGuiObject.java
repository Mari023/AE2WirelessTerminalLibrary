package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.features.Locatables;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.menu.interfaces.IInventorySlotAware;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.OptionalLong;

public abstract class WTGuiObject extends WirelessTerminalGuiObject implements IGuiItemObject, IEnergySource, IActionHost, IInventorySlotAware {

    private final FixedViewCellInventory fixedViewCellInventory;
    private final PlayerEntity myPlayer;
    private final IGridNode gridNode;

    private boolean rangeCheck = false;

    public WTGuiObject(final IWirelessTerminalHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
        final OptionalLong unparsedKey = wh.getGridKey(is);
        fixedViewCellInventory = new FixedViewCellInventory(is);
        myPlayer = ep;

        if(unparsedKey.isPresent()) {
            final IActionHost securityStation = Locatables.securityStations().get(ep.world, unparsedKey.getAsLong());
            if(securityStation != null) {
                gridNode = securityStation.getActionableNode();
            } else gridNode = null;
        } else gridNode = null;
    }

    public abstract ScreenHandlerType<?> getType();

    public abstract ItemStack getIcon();

    public boolean rangeCheck() {
        rangeCheck = super.rangeCheck();
        return rangeCheck || hasBoosterCard();
    }

    public double getRange() {
        if(isOutOfRange()) {
            return 512 * ae2wtlibConfig.INSTANCE.getOutOfRangePowerMultiplier();
        }
        return super.getRange();
    }

    public boolean isOutOfRange() {
        return !rangeCheck;
    }

    public boolean hasBoosterCard() {
        return ((IInfinityBoosterCardHolder) getItemStack().getItem()).hasBoosterCard(getItemStack());
    }

    @Override
    public IGridNode getActionableNode() {
        return gridNode;
    }

    public PlayerEntity getPlayer() {
        return myPlayer;
    }

    public FixedViewCellInventory getViewCellStorage() {
        return fixedViewCellInventory;
    }
}