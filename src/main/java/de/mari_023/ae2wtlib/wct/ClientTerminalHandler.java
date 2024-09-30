package de.mari_023.ae2wtlib.wct;

import java.util.HashMap;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.api.AE2wtlibComponents;

public class ClientTerminalHandler {
    @Nullable
    private static ClientTerminalHandler instance;

    private final Player player = Objects.requireNonNull(Minecraft.getInstance().player);
    private final CraftingTerminalHandler craftingHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
    private HashMap<Item, Long> restockAbleItems = new HashMap<>();
    private boolean restockEnabled = false;

    public static ClientTerminalHandler get() {
        if (instance == null || instance.player != Minecraft.getInstance().player)
            instance = new ClientTerminalHandler();

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
