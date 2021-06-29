package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.ILocatable;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;
import appeng.core.Api;
import appeng.tile.networking.WirelessTileEntity;
import appeng.util.item.AEItemStack;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CraftingTerminalHandler {

    private static final HashMap<UUID, CraftingTerminalHandler> players = new HashMap<>();
    private final PlayerEntity player;
    private ItemStack craftingTerminal = ItemStack.EMPTY;
    private ILocatable securityStation;
    private IGrid targetGrid;
    private IStorageGrid storageGrid;
    private IMEMonitor<IAEItemStack> itemStorageChannel;
    private int slot = -1;

    private CraftingTerminalHandler(PlayerEntity player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(PlayerEntity player) {
        if(players.containsKey(player.getUuid())) return players.get(player.getUuid());
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player.getUuid(), handler);
        return handler;
    }

    public ItemStack getCraftingTerminal() {
        PlayerInventory inv = player.inventory;
        if((!craftingTerminal.isEmpty()) && inv.contains(craftingTerminal)) return craftingTerminal;
        if(Config.allowTrinket()) {
            TrinketInventory trinketInv = (TrinketInventory) TrinketsApi.getTrinketsInventory(player);
            for(int i = 0; i < trinketInv.size(); i++) {
                ItemStack terminal = trinketInv.getStack(i);
                if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                    securityStation = null;
                    targetGrid = null;
                    slot = i;
                    return craftingTerminal = terminal;
                }
            }
        }

        for(int i = 0; i < inv.size(); i++) {
            ItemStack terminal = inv.getStack(i);
            if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                securityStation = null;
                targetGrid = null;
                slot = i;
                return craftingTerminal = terminal;
            }
        }
        return craftingTerminal = ItemStack.EMPTY;
    }

    public int getSlot() {
        if(getCraftingTerminal().isEmpty()) return slot = 0;
        return slot;
    }

    public ILocatable getSecurityStation() {
        if(getCraftingTerminal().isEmpty()) return securityStation = null;
        if(securityStation != null) return securityStation;
        final String unParsedKey = ((ItemWT) craftingTerminal.getItem()).getEncryptionKey(craftingTerminal);
        if(unParsedKey.isEmpty()) return securityStation = null;
        final long parsedKey = Long.parseLong(unParsedKey);
        return securityStation = Api.instance().registries().locatable().getLocatableBy(parsedKey);
    }

    public IGrid getTargetGrid() {
        if(getSecurityStation() == null) return targetGrid = null;
        final IGridNode n = ((IActionHost) securityStation).getActionableNode();

        if(n == null) return targetGrid = null;
        return targetGrid = n.getGrid();
    }

    public IStorageGrid getStorageGrid() {
        if(getTargetGrid() == null) return storageGrid = null;
        if(storageGrid == null) return storageGrid = targetGrid.getCache(IStorageGrid.class);
        return storageGrid;
    }

    public IMEMonitor<IAEItemStack> getItemStorageChannel() {
        if(getStorageGrid() == null) return itemStorageChannel = null;
        if(itemStorageChannel == null) return itemStorageChannel = storageGrid.getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
        return itemStorageChannel;
    }

    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;

    public boolean inRange() {
        if(getCraftingTerminal().isEmpty()) return false;
        if(((IInfinityBoosterCardHolder) craftingTerminal.getItem()).hasBoosterCard(craftingTerminal)) return true;
        sqRange = Double.MAX_VALUE;

        if(getTargetGrid() == null) return false;
        if(targetGrid == null) return false;
        if(myWap != null && myWap.getGrid() == targetGrid && testWap(myWap)) return true;

        final IMachineSet tw = targetGrid.getMachines(WirelessTileEntity.class);

        myWap = null;

        for(final IGridNode n : tw) {
            final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
            if(testWap(wap)) myWap = wap;
        }

        return myWap != null;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalCoord dc = wap.getLocation();

        if(dc.getWorld() != player.world) return false;

        final double offX = dc.x - player.getX();
        final double offY = dc.y - player.getY();
        final double offZ = dc.z - player.getZ();

        final double r = offX * offX + offY * offY + offZ * offZ;
        if(r < rangeLimit && sqRange > r && wap.isActive()) {
            sqRange = r;
            return true;
        }
        return false;
    }

    private final HashMap<Item, Long> restockAbleItems = new HashMap<>();

    public long getAccessibleAmount(ItemStack stack) {
        return stack.getCount() + (restockAbleItems.get(stack.getItem()) == null ? 0 : restockAbleItems.get(stack.getItem()));
    }

    public void setRestockAbleItems(List<AEItemStack> items) {
        restockAbleItems.clear();
        for(AEItemStack stack : items) restockAbleItems.put(stack.getItem(), stack.getStackSize());
    }
}