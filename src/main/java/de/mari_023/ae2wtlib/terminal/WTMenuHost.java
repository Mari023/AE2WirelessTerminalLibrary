package de.mari_023.ae2wtlib.terminal;

import java.util.function.BiConsumer;

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

public abstract class WTMenuHost extends WirelessTerminalMenuHost implements InternalInventoryHost {

    private final AppEngInternalInventory singularity = new AppEngInternalInventory(this, 1);

    private final AppEngInternalInventory viewCellInventory;
    private final Player myPlayer;
    private boolean rangeCheck;
    private IGridNode securityTerminalNode;
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
        IActionHost actionHost = Locatables.securityStations().get(player.level,
                ((WirelessTerminalItem) is.getItem()).getGridKey(is).getAsLong());
        if (actionHost != null)
            securityTerminalNode = actionHost.getActionableNode();
    }

    public void updateUpgrades(ItemStack stack, IUpgradeInventory upgrades) {
        upgradeInventory = upgrades;
    }

    protected void readFromNbt() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        viewCellInventory.readFromNBT(tag, "viewcells");
        singularity.readFromNBT(tag, "singularity");
    }

    public void saveChanges() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        viewCellInventory.writeToNBT(tag, "viewcells");
        singularity.writeToNBT(tag, "singularity");
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
        return rangeCheck || hasBoosterCard();
    }

    public boolean hasBoosterCard() {
        return upgradeInventory.isInstalled(AE2wtlib.QUANTUM_LINK_CARD);
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


    @Nullable
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(INV_SINGULARITY))
            return singularity;
        return null;
    }
}
