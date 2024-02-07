package de.mari_023.ae2wtlib.terminal;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibItems;

public abstract class WTMenuHost extends WirelessTerminalMenuHost<WirelessTerminalItem>
        implements InternalInventoryHost, ISegmentedInventory {
    private final AppEngInternalInventory singularityInventory = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory viewCellInventory;
    @Nullable
    private IActionHost quantumBridge;
    public static final ResourceLocation INV_SINGULARITY = AE2wtlib.id("singularity");
    private ILinkStatus linkStatus = ILinkStatus.ofDisconnected();
    private ILinkStatus quantumStatus = ILinkStatus.ofDisconnected();

    public WTMenuHost(WirelessTerminalItem item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        viewCellInventory = new AppEngInternalInventory(this, 5);
    }

    @Deprecated
    protected void readFromNbt() {
    }

    @Deprecated
    public void saveChanges() {
    }

    @Nullable
    private IGrid getLinkedGrid(ItemStack stack) {
        return getItem().getLinkedGrid(stack, getPlayer().level(), null);
    }

    @Nullable
    @Override
    public IGridNode getActionableNode() {
        if (!getPlayer().level().isClientSide() && quantumStatus.connected()) {
            assert quantumBridge != null;
            return quantumBridge.getActionableNode();
        }
        return super.getActionableNode();
    }

    @Nullable
    @Override
    public MEStorage getInventory() {
        var node = getActionableNode();
        if (node == null)
            return super.getInventory();
        return node.getGrid().getStorageService().getInventory();
    }

    @Override
    public boolean onBroadcastChanges(AbstractContainerMenu menu) {
        if (!super.onBroadcastChanges(menu))
            return false;

        linkStatus = super.getLinkStatus();
        quantumStatus = isQuantumLinked();
        if (!linkStatus.connected()) {
            if (linkStatus.equals(ILinkStatus.ofDisconnected()) || quantumStatus.connected())
                linkStatus = quantumStatus;
        }

        return true;
    }

    @Override
    public ILinkStatus getLinkStatus() {
        return linkStatus;
    }

    public void rangeCheck() {// FIXME
        // super.rangeCheck();
        isQuantumLinked();
    }

    public ILinkStatus isQuantumLinked() {
        if (!getUpgrades().isInstalled(AE2wtlibItems.instance().QUANTUM_BRIDGE_CARD))
            return ILinkStatus.ofDisconnected();
        long frequency = ItemWT.getQEFrequency(getItemStack(), singularityInventory).result();
        if (quantumBridge == null) {
            quantumBridge = ItemWT.getQuantumBridge(getItemStack(), getPlayer().level(), singularityInventory,
                    getUpgrades()).host();
            if (quantumBridge == null)
                return ILinkStatus.ofDisconnected();
        } else {
            if (quantumBridge instanceof QuantumCluster quantumCluster) {
                if (quantumCluster.getCenter() == null)
                    return ILinkStatus.ofDisconnected();
                long frequencyOther = quantumCluster.getCenter().getQEFrequency();
                if (!(frequencyOther == frequency || frequencyOther == -frequency))
                    if (ItemWT.findQuantumBridge(getPlayer().level(), frequency).invalid())
                        return ILinkStatus.ofDisconnected();
            } else if (ItemWT.findQuantumBridge(getPlayer().level(), frequency).invalid())
                return ILinkStatus.ofDisconnected();
        }
        if (quantumBridge.getActionableNode() == null)
            return ILinkStatus.ofDisconnected();
        var targetGrid = getLinkedGrid(getItemStack());
        if ((quantumBridge.getActionableNode().getGrid() == targetGrid && targetGrid != null)
                && targetGrid.getEnergyService().isNetworkPowered())
            return ILinkStatus.ofConnected();
        return ILinkStatus.ofDisconnected();
    }

    public AppEngInternalInventory getViewCellStorage() {
        return viewCellInventory;
    }

    @Override
    protected double getPowerDrainPerTick() {
        if (quantumStatus.connected()) {// TODO don't recompute this here
            // QuantumBridgeBlockEntity: 22 ae/tick
            // AbstractReportingPart (terminal): 0.5 ae/tick
            // -> 22.5 ae/tick
            return 22.5;
        }
        return super.getPowerDrainPerTick();
    }

    public void drainPower() {
        recharge();
        super.drainPower();
        recharge();
    }

    private void recharge() {
        if (!quantumStatus.connected())
            return;
        if (getItemStack().getItem() instanceof AEBasePoweredItem item) {
            double missing = item.getAEMaxPower(getItemStack()) - item.getAECurrentPower(getItemStack());
            if (getActionableNode() == null || missing <= 0)
                return;
            var energyService = getActionableNode().getGrid().getEnergyService();
            // Never discharge the network below 50%
            double safePower = energyService.getStoredPower() - energyService.getMaxStoredPower() / 2;
            if (safePower > 0) {
                double toMove = Math.min(missing, safePower);
                double extracted = energyService.extractAEPower(toMove, Actionable.MODULATE, PowerMultiplier.ONE);
                item.injectAEPower(getItemStack(), extracted, Actionable.MODULATE);
            }
        }
    }

    @Nullable
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(INV_SINGULARITY))
            return singularityInventory;
        return null;
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        viewCellInventory.writeToNBT(getItemStack().getOrCreateTag(), "viewcells");
        singularityInventory.writeToNBT(getItemStack().getOrCreateTag(), "singularity");
    }
}
