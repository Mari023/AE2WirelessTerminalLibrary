package de.mari_023.ae2wtlib.wct;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;

public class ClientTerminalHandler {
    @Nullable
    private static ClientTerminalHandler instance;

    private final Player player;
    private final CraftingTerminalHandler craftingHandler;
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();
    private boolean restockEnabled = false;

    public ClientTerminalHandler(Player player) {
        this.player = player;
        craftingHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
    }

    public static ClientTerminalHandler get(LocalPlayer player) {
        if (instance == null || instance.player != player)
            instance = new ClientTerminalHandler(player);

        return instance;
    }

    public long getAccessibleAmount(ItemStack stack) {
        return stack.getCount()
                + (restockAbleItems.get(stack.getItem()) == null ? 0 : restockAbleItems.get(stack.getItem()));
    }

    public boolean isRestockAble(ItemStack stack) {
        return restockAbleItems.containsKey(stack.getItem());
    }

    public void setRestockAbleItems(HashMap<Item, Long> items) {
        restockAbleItems = items;
    }

    public boolean isRestockEnabled() {
        return restockEnabled;
    }

    public void checkTerminal() {
        restockEnabled = craftingHandler.getCraftingTerminal().getOrDefault(AE2wtlibComponents.RESTOCK, false);
    }
}
