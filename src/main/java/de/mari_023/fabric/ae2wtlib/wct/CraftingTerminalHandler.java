package de.mari_023.fabric.ae2wtlib.wct;

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
        if(!craftingTerminal.isEmpty() && inv.contains(craftingTerminal)) return craftingTerminal;

        for(int i = 0; i < inv.size(); i++) {
            ItemStack terminal = inv.getStack(i);
            if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                return terminal;
            }
        }
        return ItemStack.EMPTY;
    }

    @Deprecated
    public static ItemStack getCraftingTerminal(PlayerEntity player) {
        PlayerInventory inv = player.inventory;
        for(int i = 0; i < inv.size(); i++) {
            ItemStack terminal = inv.getStack(i);
            if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                return terminal;
            }
        }
        return ItemStack.EMPTY;
    }
}