package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.ILocatable;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.DimensionalCoord;
import appeng.core.Api;
import appeng.tile.networking.WirelessBlockEntity;
import de.mari_023.fabric.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CraftingTerminalHandler {

    private static final HashMap<UUID, CraftingTerminalHandler> players = new HashMap<>();
    private final PlayerEntity player;
    private ItemStack craftingTerminal = ItemStack.EMPTY;
    private ILocatable securityStation;
    private IGrid targetGrid;

    private CraftingTerminalHandler(PlayerEntity player) {
        this.player = player;
    }

    public static CraftingTerminalHandler getCraftingTerminalHandler(PlayerEntity player) {
        if(players.containsKey(player.getUuid())) return players.get(player.getUuid());
        CraftingTerminalHandler handler = new CraftingTerminalHandler(player);
        players.put(player.getUuid(), handler);
        return handler;
    }

    public ItemStack getCraftingTerminal() {//TODO trinkets/curios
        PlayerInventory inv = player.inventory;
        if((!craftingTerminal.isEmpty()) && inv.contains(craftingTerminal)) return craftingTerminal;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack terminal = inv.getStack(i);
            if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                securityStation = null;
                targetGrid = null;
                return craftingTerminal = terminal;
            }
        }
        return ItemStack.EMPTY;
    }

    public ILocatable getSecurityStation() {
        if(getCraftingTerminal().isEmpty()) return securityStation = null;
        if(securityStation != null) return securityStation;
        final String unParsedKey = ((ItemWT) craftingTerminal.getItem()).getEncryptionKey(craftingTerminal);
        if(unParsedKey.isEmpty()) return null;
        final long parsedKey = Long.parseLong(unParsedKey);
        return securityStation = Api.instance().registries().locatable().getLocatableBy(parsedKey);
    }

    public IGrid getTargetGrid() {
        if(getSecurityStation() == null) return targetGrid = null;
        final IGridNode n = ((IActionHost) securityStation).getActionableNode();

        if(n != null) {
            return targetGrid = n.getGrid();
        }
        return targetGrid = null;
    }

    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;

    public boolean inRange() {
        if(getCraftingTerminal().isEmpty()) return false;
        if(((IInfinityBoosterCardHolder) craftingTerminal.getItem()).hasBoosterCard(craftingTerminal)) return true;
        sqRange = Double.MAX_VALUE;

        if(getTargetGrid() == null) return false;
        if(targetGrid != null) {
            if(myWap != null) {
                if(myWap.getGrid() == targetGrid) {
                    if(testWap(myWap)) return true;
                }
            }

            final IMachineSet tw = targetGrid.getMachines(WirelessBlockEntity.class);

            myWap = null;

            for (final IGridNode n : tw) {
                final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
                if(testWap(wap)) {
                    myWap = wap;
                }
            }

            return myWap != null;
        }
        return false;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalCoord dc = wap.getLocation();

        if(dc.getWorld() == player.world) {
            final double offX = dc.x - player.getX();
            final double offY = dc.y - player.getY();
            final double offZ = dc.z - player.getZ();

            final double r = offX * offX + offY * offY + offZ * offZ;
            if(r < rangeLimit && sqRange > r) {
                if(wap.isActive()) {
                    sqRange = r;
                    return true;
                }
            }
        }
        return false;
    }
}