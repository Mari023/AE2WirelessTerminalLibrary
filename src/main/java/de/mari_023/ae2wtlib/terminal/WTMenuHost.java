package de.mari_023.ae2wtlib.terminal;

import java.util.function.BiConsumer;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.ISegmentedInventory;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.me.cluster.implementations.QuantumCluster;
import de.mari_023.ae2wtlib.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibConfig;

import appeng.api.features.Locatables;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public abstract class WTMenuHost extends WirelessTerminalMenuHost implements InternalInventoryHost, ISegmentedInventory {

    private final AppEngInternalInventory singularityInventory = new AppEngInternalInventory(this, 1);

    private final AppEngInternalInventory viewCellInventory;
    private final Player myPlayer;
    private boolean rangeCheck;
    private IActionHost securityTerminal;
    private IGridNode securityTerminalNode;

    private IActionHost quantumBridge;
    private IUpgradeInventory upgradeInventory;
    public static final ResourceLocation INV_SINGULARITY = new ResourceLocation(AE2wtlib.MOD_NAME, "singularity");

    public WTMenuHost(final Player player, @Nullable Integer inventorySlot, final ItemStack is,
                      BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, is, returnToMainMenu);
        viewCellInventory = new AppEngInternalInventory(this, 5);
        myPlayer = player;
        upgradeInventory = UpgradeInventories.forItem(is, 2, this::updateUpgrades);

        if (((WirelessTerminalItem) is.getItem()).getGridKey(is).isEmpty())
            return;
        securityTerminal = Locatables.securityStations().get(player.level,
                ((WirelessTerminalItem) is.getItem()).getGridKey(is).getAsLong());
        if (securityTerminal != null)
            securityTerminalNode = securityTerminal.getActionableNode();
    }

    public void updateUpgrades(ItemStack stack, IUpgradeInventory upgrades) {
        upgradeInventory = upgrades;
    }

    protected void readFromNbt() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        viewCellInventory.readFromNBT(tag, "viewcells");
        singularityInventory.readFromNBT(tag, "singularity");
    }

    public void saveChanges() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        viewCellInventory.writeToNBT(tag, "viewcells");
        singularityInventory.writeToNBT(tag, "singularity");
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        saveChanges();
    }

    @Override
    public IGridNode getActionableNode() {
        IGridNode node = super.getActionableNode();
        if (node != null)
            return node;
        return securityTerminalNode;
    }

    public boolean rangeCheck() {
        rangeCheck = super.rangeCheck();
        return isQuantumLinked() || rangeCheck;
    }

    public boolean hasQuantumUpgrade() {
        return upgradeInventory.isInstalled(AE2wtlib.QUANTUM_BRIDGE_CARD);
    }

    public boolean isQuantumLinked() {
        if (getPlayer().getLevel().isClientSide()) return true;

        if (!hasQuantumUpgrade()) return false;
        long frequency = getQEFrequency();
        if (frequency == 0) return false;
        if (quantumBridge == null) {
            if (!findQuantumBridge(frequency)) return false;
        } else {
            if (quantumBridge instanceof QuantumCluster quantumCluster) {
                long frequencyOther = quantumCluster.getCenter().getQEFrequency();
                if (!(frequencyOther == frequency || frequencyOther == -frequency))
                    if (!findQuantumBridge(frequency)) return false;
            } else if (!findQuantumBridge(frequency)) return false;
        }
        if(quantumBridge.getActionableNode() == null) return false;
        return quantumBridge.getActionableNode().getGrid() == securityTerminalNode.getGrid();
    }

    private long getQEFrequency() {
        final ItemStack is = singularityInventory.getStackInSlot(0);
        if (!is.isEmpty()) {
            final CompoundTag c = is.getTag();
            if (c != null) {
                return c.getLong("freq");
            }
        }
        return 0;
    }

    private boolean findQuantumBridge(long frequency) {
        quantumBridge = Locatables.quantumNetworkBridges().get(getPlayer().getLevel(), frequency);
        if(quantumBridge == null) quantumBridge = Locatables.quantumNetworkBridges().get(getPlayer().getLevel(), -frequency);
        return quantumBridge != null;
    }

    public Player getPlayer() {
        return myPlayer;
    }

    public AppEngInternalInventory getViewCellStorage() {
        return viewCellInventory;
    }

    @Override
    protected void setPowerDrainPerTick(double powerDrainPerTick) {
        if (rangeCheck) {
            super.setPowerDrainPerTick(powerDrainPerTick);
        } else {
            super.setPowerDrainPerTick(AE2wtlibConfig.INSTANCE.getOutOfRangePower());
        }
    }

    public boolean drainPower() {
        if(!super.drainPower()) return false;
        recharge();
        return true;
    }

    private void recharge() {
        if(quantumBridge == null) return;
        if(getItemStack().getItem() instanceof AEBasePoweredItem item) {
            double currentPower = item.getAECurrentPower(getItemStack());
            double maxPower = item.getAEMaxPower(getItemStack());
            double missing = maxPower - currentPower;
            if(getActionableNode() == null) return;
            double extracted = getActionableNode().getGrid().getEnergyService().extractAEPower(missing, Actionable.MODULATE, PowerMultiplier.ONE);
            item.injectAEPower(getItemStack(), extracted, Actionable.MODULATE);
        }
    }


    @Nullable
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(INV_SINGULARITY))
            return singularityInventory;
        return null;
    }

    public boolean stillValid() {
        return ensureItemStillInSlot();
    }

    public IActionHost getActionHost() {
        return securityTerminal;
    }

    protected boolean ensureItemStillInSlot() {
        if(getSlot() != null) return super.ensureItemStillInSlot();
        return Platform.isStillPresentTrinkets(getPlayer(), getItemStack());
    }
}
