package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.features.Locatables;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import appeng.util.Platform;
import appeng.util.inv.AppEngInternalInventory;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.results.ActionHostResult;
import de.mari_023.ae2wtlib.terminal.results.GridResult;
import de.mari_023.ae2wtlib.terminal.results.LongResult;
import de.mari_023.ae2wtlib.terminal.results.Status;
import de.mari_023.ae2wtlib.wut.WUTHandler;

public abstract class ItemWT extends WirelessTerminalItem implements IUniversalWirelessTerminalItem {
    public ItemWT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level w, final Player player, final InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (checkUniversalPreconditions(is, player)) {
            open(player, is, MenuLocators.forHand(player, hand), false);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, is);
    }

    @Nullable
    public ItemMenuHost getMenuHost(Player player, int slot, ItemStack stack, @Nullable BlockPos pos) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(stack)).wTMenuHostFactory().create(player,
                slot, stack, (p, subMenu) -> tryOpen(player, MenuLocators.forInventorySlot(slot), stack, true));
    }

    public static ActionHostResult findQuantumBridge(Level level, long frequency) {
        IActionHost quantumBridge = Locatables.quantumNetworkBridges().get(level, frequency);
        if (quantumBridge == null)
            quantumBridge = Locatables.quantumNetworkBridges().get(level, -frequency);
        if (quantumBridge == null)
            return ActionHostResult.invalid(Status.BridgeNotFound);
        return ActionHostResult.valid(quantumBridge);
    }

    public static LongResult getQEFrequency(ItemStack stack, @Nullable AppEngInternalInventory inventory) {
        if (inventory == null) {
            inventory = new AppEngInternalInventory(null, 1);
            inventory.readFromNBT(stack.getOrCreateTag(), "singularity");
        }
        final ItemStack is = inventory.getStackInSlot(0);
        if (is.isEmpty())
            return LongResult.invalid(Status.NoSingularity);
        final CompoundTag c = is.getTag();

        if (c == null)
            return LongResult.invalid(Status.GenericInvalid);
        return LongResult.valid(c.getLong("freq"));
    }

    private static boolean hasQuantumUpgrade(ItemStack stack, @Nullable IUpgradeInventory inventory) {
        if (inventory == null)
            inventory = UpgradeInventories.forItem(stack, WUTHandler.getUpgradeCardCount());
        return inventory.isInstalled(AE2wtlib.QUANTUM_BRIDGE_CARD);
    }

    public static ActionHostResult getQuantumBridge(ItemStack itemStack, Level level,
            @Nullable AppEngInternalInventory singularityInventory, @Nullable IUpgradeInventory upgradeInventory) {
        if (level.isClientSide())
            return ActionHostResult.invalid(Status.NotServer);

        Status status = Status.Valid;

        if (!hasQuantumUpgrade(itemStack, upgradeInventory))
            status = Status.NoUpgrade;
        LongResult frequency = getQEFrequency(itemStack, singularityInventory);
        if (!frequency.valid()) {
            status = status.isValid() ? frequency.status() : Status.GenericInvalid;
        }
        if (!status.isValid())
            return ActionHostResult.invalid(status);
        return findQuantumBridge(level, frequency.result());
    }

    private GridResult getLinkedGrid(ItemStack item, Level level) {
        if (!(level instanceof ServerLevel serverLevel))
            return GridResult.invalid(Status.NotServer);

        GridResult grid = getAccessPointLinkedGrid(item, serverLevel);
        if (grid.valid())
            return grid;

        var quantumBridgeResult = getQuantumBridge(item, level, null, null);
        if (quantumBridgeResult.invalid()) {
            return switch (grid.status()) {
                case NotFound, NotLinked -> quantumBridgeResult.status() == Status.GenericInvalid ? grid
                        : GridResult.invalid(quantumBridgeResult);
                default -> grid;
            };
        }
        var quantumBridge = quantumBridgeResult.host();

        assert quantumBridge != null;// can't happen if the result is valid
        if (quantumBridge.getActionableNode() == null)
            return GridResult.invalid(Status.NotFound);
        if (!quantumBridge.getActionableNode().isPowered())
            return GridResult.invalid(Status.NotPowered);
        return GridResult.valid(quantumBridge.getActionableNode().getGrid());
    }

    private GridResult getAccessPointLinkedGrid(ItemStack item, ServerLevel level) {
        var linkedPos = getLinkedPosition(item);
        if (linkedPos == null)
            return GridResult.invalid(Status.NotLinked);

        var linkedLevel = level.getServer().getLevel(linkedPos.dimension());
        if (linkedLevel == null)
            return GridResult.invalid(Status.NotFound);

        var be = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
        if (!(be instanceof IWirelessAccessPoint accessPoint))
            return GridResult.invalid(Status.NotFound);

        var grid = accessPoint.getGrid();
        if (grid == null)
            return GridResult.invalid(Status.NotFound);

        if (!grid.getEnergyService().isNetworkPowered())
            return GridResult.invalid(Status.NotPowered);
        return GridResult.valid(grid);
    }

    @Nullable
    public IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Player sendMessagesTo) {
        GridResult grid = getLinkedGrid(item, level);
        if (grid.status().error != null && sendMessagesTo != null && !level.isClientSide()) {
            sendMessagesTo.displayClientMessage(grid.status().error, true);
        }
        return grid.grid();
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    @Deprecated
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return ItemStack.EMPTY;
        return ItemStack.of(hostItem.getOrCreateTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    @Deprecated
    public static void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        if (savedItem.isEmpty())
            wctTag.remove(slot);
        else
            wctTag.put(slot, savedItem.save(new CompoundTag()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return false;
        return hostItem.getOrCreateTag().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public static void setBoolean(ItemStack hostItem, boolean b, String key) {
        if (!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem))
            return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        wctTag.putBoolean(key, b);
    }
}
