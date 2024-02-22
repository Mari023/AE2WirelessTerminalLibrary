package de.mari_023.ae2wtlib.terminal;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ILinkStatus;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.contents.StackDependentSupplier;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.SupplierInternalInventory;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibItems;

public abstract class WTMenuHost extends WirelessTerminalMenuHost<ItemWT>
        implements ISegmentedInventory {
    private final SupplierInternalInventory<InternalInventory> singularityInventory;
    private final SupplierInternalInventory<InternalInventory> viewCellInventory;
    @Nullable
    private IActionHost quantumBridge;
    public static final ResourceLocation INV_SINGULARITY = AE2wtlib.id("singularity");
    private ILinkStatus linkStatus = ILinkStatus.ofDisconnected();
    private ILinkStatus quantumStatus = ILinkStatus.ofDisconnected();

    public WTMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        viewCellInventory = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(this::getItemStack, stack -> createInv(player, stack, "viewcells", 5)));
        singularityInventory = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(this::getItemStack, stack -> createInv(player, stack, "singularity", 1)));
    }

    @Nullable
    private IGrid getLinkedGrid(ItemStack stack) {// TODO use ae2 method
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

    public void updateLinkStatus() {
        super.updateLinkStatus();
        linkStatus = super.getLinkStatus();
        if (linkStatus.connected())
            return;
        if (linkStatus.equals(ILinkStatus.ofDisconnected()) || quantumStatus.connected())
            linkStatus = quantumStatus;
    }

    @Override
    public ILinkStatus getLinkStatus() {
        return linkStatus;
    }

    public void updateConnectedAccessPoint() {
        super.updateConnectedAccessPoint();
        quantumStatus = isQuantumLinked();
    }

    private ILinkStatus isQuantumLinked() {// TODO add reasons when it isn't connected
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

    public InternalInventory getViewCellStorage() {
        return viewCellInventory;
    }

    @Override
    protected double getPowerDrainPerTick() {
        if (quantumStatus.connected()) {
            // QuantumBridgeBlockEntity: 22 ae/tick
            // AbstractReportingPart (terminal): 0.5 ae/tick
            // -> 22.5 ae/tick
            return 22.5;
        }
        return super.getPowerDrainPerTick();
    }

    public boolean consumeIdlePower(Actionable action) {
        if (action == Actionable.SIMULATE)
            recharge();
        boolean success = super.consumeIdlePower(action);
        if (action == Actionable.SIMULATE)
            recharge();
        return success;
    }

    private void recharge() {
        if (!quantumStatus.connected())
            return;
        if (!(getItemStack().getItem() instanceof AEBasePoweredItem item))
            return;
        double missing = item.getAEMaxPower(getItemStack()) - item.getAECurrentPower(getItemStack());
        if (getActionableNode() == null || missing <= 0)
            return;
        var energyService = getActionableNode().getGrid().getEnergyService();
        // Never discharge the network below 50%
        double safePower = energyService.getStoredPower() - energyService.getMaxStoredPower() / 2;
        if (safePower <= 0)
            return;
        double toMove = Math.min(missing, safePower);
        double extracted = energyService.extractAEPower(toMove, Actionable.MODULATE, PowerMultiplier.ONE);
        item.injectAEPower(getItemStack(), extracted, Actionable.MODULATE);
    }

    @Nullable
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(INV_SINGULARITY))
            return singularityInventory;
        return null;
    }

    protected static InternalInventory createInv(Player player, ItemStack stack, String name, int size) {
        var inv = new AppEngInternalInventory(new InternalInventoryHost() {
            @Override
            public void saveChangedInventory(AppEngInternalInventory inv) {
                inv.writeToNBT(stack.getOrCreateTag(), name);
            }

            @Override
            public boolean isClientSide() {
                return player.level().isClientSide();
            }
        }, size);
        if (stack.getTag() != null) {
            inv.readFromNBT(stack.getTag(), name);
        }
        return inv;
    }
}
