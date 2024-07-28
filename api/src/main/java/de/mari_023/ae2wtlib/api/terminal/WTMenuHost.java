package de.mari_023.ae2wtlib.api.terminal;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.features.Locatables;
import appeng.api.ids.AEComponents;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.storage.SupplierStorage;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.contents.StackDependentSupplier;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.me.storage.NullInventory;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.SupplierInternalInventory;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.results.ActionHostResult;
import de.mari_023.ae2wtlib.api.results.LongResult;
import de.mari_023.ae2wtlib.api.results.Status;

public abstract class WTMenuHost extends WirelessTerminalMenuHost<ItemWT>
        implements ISegmentedInventory {
    private final SupplierInternalInventory<InternalInventory> singularityInventory;
    private final SupplierInternalInventory<InternalInventory> viewCellInventory;
    @Nullable
    private IActionHost quantumBridge;
    public static final ResourceLocation INV_SINGULARITY = AE2wtlibAPI.id("singularity");
    private final MEStorage storage;
    @UnknownNullability
    private ILinkStatus linkStatus;
    @UnknownNullability
    private ILinkStatus quantumStatus;
    private final String closeHotkey;

    public WTMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.storage = new SupplierStorage(new StackDependentSupplier<>(
                this::getItemStack, this::getStorageFromStack));
        viewCellInventory = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(this::getItemStack,
                        stack -> createInv(player, stack, AE2wtlibComponents.VIEW_CELL_INVENTORY, 5)));
        singularityInventory = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(this::getItemStack, stack -> createSingularityInv(player, stack)));

        String close = super.getCloseHotkey();
        var wtDefinition = WTDefinition.ofOrNull(getItemStack());
        if (wtDefinition != null)
            close = wtDefinition.hotkeyName();
        assert close != null;
        closeHotkey = close;
    }

    public String getCloseHotkey() {
        return closeHotkey;
    }

    @Override
    public MEStorage getInventory() {
        return this.storage;
    }

    @Nullable
    private MEStorage getStorageFromStack(ItemStack stack) {
        updateConnectedAccessPoint();
        IGridNode node = getActionableNode();
        if (node == null)
            return NullInventory.of();
        IGrid targetGrid = node.getGrid();
        if (targetGrid != null) {
            return targetGrid.getStorageService().getInventory();
        }
        return NullInventory.of();
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
        if (quantumStatus.equals(ILinkStatus.ofDisconnected()))
            return;
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

    public ActionHostResult findQuantumBridge(Level level, long frequency) {
        IActionHost quantumBridge = Locatables.quantumNetworkBridges().get(level, frequency);
        if (quantumBridge == null)
            quantumBridge = Locatables.quantumNetworkBridges().get(level, -frequency);
        if (quantumBridge == null)
            return ActionHostResult.invalid(Status.BridgeNotFound);
        return ActionHostResult.valid(quantumBridge);
    }

    public LongResult getQEFrequency() {
        ItemStack singularity = getItemStack().getOrDefault(AE2wtlibComponents.SINGULARITY, ItemStack.EMPTY);

        if (singularity.isEmpty())
            return LongResult.invalid(Status.NoSingularity);
        Long singularityID = singularity.get(AEComponents.ENTANGLED_SINGULARITY_ID);
        if (singularityID == null)
            return LongResult.invalid(Status.GenericInvalid);
        return LongResult.valid(singularityID);
    }

    private ILinkStatus isQuantumLinked() {
        Status status = Status.Valid;
        if (!AE2wtlibAPI.hasQuantumBridgeCard(this::getUpgrades))
            status = Status.NoUpgrade;
        LongResult f = getQEFrequency();
        if (!f.valid()) {
            status = status.isValid() ? f.status() : Status.GenericInvalid;
        }
        if (!status.isValid())
            return status.toILinkStatus();
        long frequency = f.result();
        if (quantumBridge == null) {
            var qb = findQuantumBridge(getPlayer().level(), frequency);
            quantumBridge = qb.host();
            if (qb.invalid())
                return qb.status().toILinkStatus();
            assert quantumBridge != null;
        } else if (quantumBridge instanceof QuantumCluster quantumCluster) {
            if (quantumCluster.getCenter() == null)
                return Status.BridgeNotFound.toILinkStatus();
            long frequencyOther = quantumCluster.getCenter().getQEFrequency();
            if (!(frequencyOther == frequency || frequencyOther == -frequency))
                if (findQuantumBridge(getPlayer().level(), frequency).invalid())
                    return Status.BridgeNotFound.toILinkStatus();
        } else {
            var qb = findQuantumBridge(getPlayer().level(), frequency);
            quantumBridge = qb.host();
            if (qb.invalid())
                return qb.status().toILinkStatus();
            assert quantumBridge != null;
        }

        if (quantumBridge.getActionableNode() == null || quantumBridge.getActionableNode().getGrid() == null)
            return Status.BridgeNotFound.toILinkStatus();
        var targetGrid = getItem().getLinkedGrid(getItemStack(), getPlayer().level(), null);
        if (quantumBridge.getActionableNode().getGrid() != targetGrid && targetGrid != null)
            return Status.DifferentNetworks.toILinkStatus();
        if (!quantumBridge.getActionableNode().getGrid().getEnergyService().isNetworkPowered())
            return Status.NotPowered.toILinkStatus();
        return ILinkStatus.ofConnected();
    }

    public InternalInventory getViewCellStorage() {
        return viewCellInventory;
    }

    @Override
    protected double getPowerDrainPerTick() {
        if (quantumStatus.connected() || Status.NotPowered.is(quantumStatus)) {
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
        if (!quantumStatus.connected() && !Status.NotPowered.is(quantumStatus))
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

    protected static InternalInventory createInv(Player player, ItemStack stack,
            DataComponentType<ItemContainerContents> componentType, int size) {
        var craftingGrid = new AppEngInternalInventory(new InternalInventoryHost() {
            @Override
            public void saveChangedInventory(AppEngInternalInventory inv) {
                stack.set(componentType, inv.toItemContainerContents());
            }

            @Override
            public boolean isClientSide() {
                return player.level().isClientSide();
            }
        }, size);
        craftingGrid
                .fromItemContainerContents(stack.getOrDefault(componentType, ItemContainerContents.EMPTY));
        return craftingGrid;
    }

    private static InternalInventory createSingularityInv(Player player, ItemStack stack) {
        var inv = new AppEngInternalInventory(new InternalInventoryHost() {
            @Override
            public void saveChangedInventory(AppEngInternalInventory inv) {
                stack.set(AE2wtlibComponents.SINGULARITY, inv.getStackInSlot(0));
            }

            @Override
            public boolean isClientSide() {
                return player.level().isClientSide();
            }
        }, 1, 1);
        inv.setItemDirect(0, stack.getOrDefault(AE2wtlibComponents.SINGULARITY, ItemStack.EMPTY));
        return inv;
    }
}
