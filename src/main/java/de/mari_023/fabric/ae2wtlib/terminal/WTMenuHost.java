package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.features.Locatables;
import appeng.api.networking.IGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.ISubMenu;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public abstract class WTMenuHost extends WirelessTerminalMenuHost {

    private final ViewCellInventory viewCellInventory;
    private final Player myPlayer;
    private boolean rangeCheck;
    private IGridNode securityTerminalNode;
    private IUpgradeInventory upgradeInventory;

    public WTMenuHost(final Player player, @Nullable Integer inventorySlot, final ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, is, returnToMainMenu);
        viewCellInventory = new ViewCellInventory(is);
        myPlayer = player;
        var actionHost = Locatables.securityStations().get(player.level, ((WirelessTerminalItem) is.getItem()).getGridKey(is).getAsLong());
        if(actionHost != null) securityTerminalNode = actionHost.getActionableNode();
        upgradeInventory = UpgradeInventories.forItem(is,2, this::updateUpgrades);
    }

    public void updateUpgrades(ItemStack stack, IUpgradeInventory upgrades) {
        upgradeInventory = upgrades;
    }

    @Override
    public IGridNode getActionableNode() {
        IGridNode node = super.getActionableNode();
        if(node != null) return node;
        return securityTerminalNode;
    }

    public abstract MenuType<?> getType();

    public boolean rangeCheck() {
        rangeCheck = super.rangeCheck();
        return rangeCheck || hasBoosterCard();
    }

    public boolean hasBoosterCard() {
        return upgradeInventory.isInstalled(AE2wtlib.INFINITY_BOOSTER);
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