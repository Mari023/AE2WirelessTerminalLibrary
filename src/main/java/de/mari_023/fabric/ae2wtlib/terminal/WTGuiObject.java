package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.features.Locatables;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.networking.WirelessBlockEntity;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.menu.interfaces.IInventorySlotAware;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.OptionalLong;

public abstract class WTGuiObject extends WirelessTerminalGuiObject implements IGuiItemObject, IEnergySource, IActionHost, IInventorySlotAware {

    private final FixedViewCellInventory fixedViewCellInventory;
    private IGrid targetGrid;
    private final PlayerEntity myPlayer;
    private IMEMonitor<IAEItemStack> itemStorage;
    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;
    private double myRange = Double.MAX_VALUE;
    private final IGridNode gridNode;

    public WTGuiObject(final IWirelessTerminalHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
        final OptionalLong unparsedKey = wh.getGridKey(is);
        fixedViewCellInventory = new FixedViewCellInventory(is);
        myPlayer = ep;

        if(unparsedKey.isPresent()) {
            final IActionHost securityStation = Locatables.securityStations().get(ep.world, unparsedKey.getAsLong());
            if(securityStation != null) {
                gridNode = securityStation.getActionableNode();
                if(gridNode == null) return;
                targetGrid = gridNode.getGrid();
                IStorageService sg = targetGrid.getStorageService();
                itemStorage = sg.getInventory(StorageChannels.items());
            } else gridNode = null;
        } else gridNode = null;
    }

    public abstract ScreenHandlerType<?> getType();

    public abstract ItemStack getIcon();

    public boolean rangeCheck() {
        return !notInRange();
    }

    public boolean notInRange() {
        boolean hasBoosterCard = ((IInfinityBoosterCardHolder) getItemStack().getItem()).hasBoosterCard(getItemStack());
        sqRange = myRange = Double.MAX_VALUE;

        if(targetGrid != null && itemStorage != null) {
            if(myWap != null) {
                if(myWap.getGrid() == targetGrid) return !testWap(myWap) && !hasBoosterCard;
                return !hasBoosterCard;
            } else isOutOfRange = true;

            for(final WirelessBlockEntity n : targetGrid.getMachines(WirelessBlockEntity.class)) {
                if(testWap(n)) myWap = n;
            }

            return myWap == null && !hasBoosterCard;
        }
        return !hasBoosterCard;
    }

    public double getRange() {
        return myRange;
    }

    private boolean isOutOfRange;

    public boolean isOutOfRange() {
        return isOutOfRange;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalBlockPos dc = wap.getLocation();

        if(dc.getLevel() == myPlayer.world) {
            final double offX = dc.getPos().getX() - myPlayer.getX();
            final double offY = dc.getPos().getY() - myPlayer.getY();
            final double offZ = dc.getPos().getZ() - myPlayer.getZ();

            final double r = offX * offX + offY * offY + offZ * offZ;
            if(r < rangeLimit && sqRange > r && wap.isActive()) {
                sqRange = r;
                myRange = Math.sqrt(r);
                isOutOfRange = false;
                return true;
            }
        }
        isOutOfRange = true;
        return false;
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