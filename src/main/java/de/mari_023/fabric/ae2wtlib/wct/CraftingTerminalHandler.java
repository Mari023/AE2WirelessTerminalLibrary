package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.Locatables;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.MEStorage;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.networking.WirelessBlockEntity;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.AE2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.trinket.CombinedTrinketInventory;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import java.util.HashMap;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CraftingTerminalHandler {

    private static final HashMap<UUID, CraftingTerminalHandler> players = new HashMap<>();//TODO clear on leave (client)
    private final Player player;
    private ItemStack craftingTerminal = ItemStack.EMPTY;
    private IActionHost securityStation;
    private IGrid targetGrid;
    private IStorageService storageGrid;
    private MEStorage itemStorageChannel;
    private MenuLocator locator;
    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();

    private CraftingTerminalHandler(Player player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(Player player) {
        if(players.containsKey(player.getUUID())) {
            if(player == players.get(player.getUUID()).player ||
                    (!(player instanceof ServerPlayer) && (players.get(player.getUUID()).player instanceof ServerPlayer)))
                return players.get(player.getUUID());
            removePlayer(player);
        }
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player.getUUID(), handler);
        return handler;
    }

    public static void removePlayer(Player player) {//TODO remove on disconnect (server)
        players.remove(player.getUUID());
    }

    public void invalidateCache() {
        craftingTerminal = ItemStack.EMPTY;
        locator = null;
        securityStation = null;
        targetGrid = null;
        storageGrid = null;
        itemStorageChannel = null;
        myWap = null;
        sqRange = Double.MAX_VALUE;
        restockAbleItems.clear();
    }

    public ItemStack getCraftingTerminal() {
        Inventory inv = player.getInventory();
        if((!craftingTerminal.isEmpty()) && inv.contains(craftingTerminal)) return craftingTerminal;
        if(AE2wtlibConfig.INSTANCE.allowTrinket()) {
            CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
            for(int i = 0; i < trinketInv.size(); i++) {
                ItemStack terminal = trinketInv.getStackInSlot(i);
                if(terminal.getItem() instanceof WirelessCraftingTerminalItem || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                    securityStation = null;
                    targetGrid = null;
                    locator = new TrinketLocator(i);
                    return craftingTerminal = terminal;
                }
            }
        }

        for(int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack terminal = inv.getItem(i);
            if(terminal.getItem() instanceof WirelessCraftingTerminalItem || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                securityStation = null;
                targetGrid = null;
                locator = MenuLocators.forInventorySlot(i);
                return craftingTerminal = terminal;
            }
        }
        invalidateCache();
        return ItemStack.EMPTY;
    }

    @Nullable
    public MenuLocator getLocator() {
        if(getCraftingTerminal().isEmpty()) return null;
        return locator;
    }

    public IActionHost getSecurityStation() {
        if(getCraftingTerminal().isEmpty()) return securityStation = null;
        if(securityStation != null) return securityStation;
        final OptionalLong unParsedKey = ((WirelessTerminalItem) craftingTerminal.getItem()).getGridKey(craftingTerminal);
        if(unParsedKey.isEmpty()) return securityStation = null;
        final long parsedKey = unParsedKey.getAsLong();
        return securityStation = Locatables.securityStations().get(player.level, parsedKey);
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

    public MEStorage getItemStorageChannel() {
        if(getStorageGrid() == null) return itemStorageChannel = null;
        if(itemStorageChannel == null)
            return itemStorageChannel = storageGrid.getInventory();
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

        if(dc.getLevel() != player.level) return false;

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