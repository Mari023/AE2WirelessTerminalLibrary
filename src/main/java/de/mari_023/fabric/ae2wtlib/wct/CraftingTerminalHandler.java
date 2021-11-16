package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.Locatables;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.AEItemKey;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.networking.WirelessBlockEntity;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.trinket.CombinedTrinketInventory;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;

public class CraftingTerminalHandler {

    private static final HashMap<UUID, CraftingTerminalHandler> players = new HashMap<>();//TODO clear on leave (client)
    private final PlayerEntity player;
    private ItemStack craftingTerminal = ItemStack.EMPTY;
    private IActionHost securityStation;
    private IGrid targetGrid;
    private IStorageService storageGrid;
    private IMEMonitor<AEItemKey> itemStorageChannel;
    private int slot = -1;
    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();

    private CraftingTerminalHandler(PlayerEntity player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(PlayerEntity player) {
        if(players.containsKey(player.getUuid())) {
            if(player == players.get(player.getUuid()).player ||
                    (!(player instanceof ServerPlayerEntity) && (players.get(player.getUuid()).player instanceof ServerPlayerEntity)))
                return players.get(player.getUuid());
            removePlayer(player);
        }
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player.getUuid(), handler);
        return handler;
    }

    public static void removePlayer(PlayerEntity player) {//TODO remove on disconnect (server)
        players.remove(player.getUuid());
    }

    public void invalidateCache() {
        craftingTerminal = ItemStack.EMPTY;
        slot = -1;
        securityStation = null;
        targetGrid = null;
        storageGrid = null;
        itemStorageChannel = null;
        myWap = null;
        sqRange = Double.MAX_VALUE;
        restockAbleItems.clear();
    }

    public ItemStack getCraftingTerminal() {
        PlayerInventory inv = player.getInventory();
        if((!craftingTerminal.isEmpty()) && inv.contains(craftingTerminal)) return craftingTerminal;
        if(ae2wtlibConfig.INSTANCE.allowTrinket()) {
            CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
            for(int i = 0; i < trinketInv.size(); i++) {
                ItemStack terminal = trinketInv.getStackInSlot(i);
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
        invalidateCache();
        return ItemStack.EMPTY;
    }

    public int getSlot() {
        if(getCraftingTerminal().isEmpty()) return slot = 0;
        return slot;
    }

    public IActionHost getSecurityStation() {
        if(getCraftingTerminal().isEmpty()) return securityStation = null;
        if(securityStation != null) return securityStation;
        final OptionalLong unParsedKey = ((ItemWT) craftingTerminal.getItem()).getGridKey(craftingTerminal);
        if(unParsedKey.isEmpty()) return securityStation = null;
        final long parsedKey = unParsedKey.getAsLong();
        return securityStation = Locatables.securityStations().get(player.world, parsedKey);
    }

    public IGrid getTargetGrid() {
        if(getSecurityStation() == null) return targetGrid = null;
        final IGridNode n = securityStation.getActionableNode();

        if(n == null) return targetGrid = null;
        return targetGrid = n.getGrid();
    }

    public IStorageService getStorageGrid() {
        if(getTargetGrid() == null) return storageGrid = null;
        if(storageGrid == null) return storageGrid = targetGrid.getStorageService();
        return storageGrid;
    }

    public IMEMonitor<AEItemKey> getItemStorageChannel() {
        if(getStorageGrid() == null) return itemStorageChannel = null;
        if(itemStorageChannel == null)
            return itemStorageChannel = storageGrid.getInventory(StorageChannels.items());
        return itemStorageChannel;
    }

    public boolean inRange() {
        if(getCraftingTerminal().isEmpty()) return false;
        if(((IInfinityBoosterCardHolder) craftingTerminal.getItem()).hasBoosterCard(craftingTerminal)) return true;
        sqRange = Double.MAX_VALUE;

        if(getTargetGrid() == null) return false;
        if(targetGrid == null) return false;
        if(myWap != null && myWap.getGrid() == targetGrid && testWap(myWap)) return true;

        final Set<WirelessBlockEntity> tw = targetGrid.getMachines(WirelessBlockEntity.class);

        myWap = null;

        for(final WirelessBlockEntity n : tw) {
            if(testWap(n)) myWap = n;
        }

        return myWap != null;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalBlockPos dc = wap.getLocation();

        if(dc.getLevel() != player.world) return false;

        final double offX = dc.getPos().getX() - player.getX();
        final double offY = dc.getPos().getY() - player.getY();
        final double offZ = dc.getPos().getZ() - player.getZ();

        final double r = offX * offX + offY * offY + offZ * offZ;
        if(r < rangeLimit && sqRange > r && wap.isActive()) {
            sqRange = r;
            return true;
        }
        return false;
    }

    public long getAccessibleAmount(ItemStack stack) {
        return stack.getCount() + (restockAbleItems.get(stack.getItem()) == null ? 0 : restockAbleItems.get(stack.getItem()));
    }

    public void setRestockAbleItems(HashMap<Item, Long> items) {
        restockAbleItems = items;
    }
}