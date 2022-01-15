package de.mari_023.ae2wtlib.client;

import java.util.HashMap;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;

import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;

public class NetworkingClient {
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "update_restock"),
                (client, handler, buf, responseSender) -> {
                    buf.retain();
                    client.execute(() -> {
                        if (client.player == null)
                            return;
                        client.player.getInventory().getItem(buf.readInt()).setCount(buf.readInt());
                        buf.release();
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "update_wut"),
                (client, handler, buf, responseSender) -> {
                    buf.retain();
                    client.execute(() -> {
                        if (client.player == null)
                            return;
                        MenuLocator locator = MenuLocators.readFromPacket(buf);
                        CompoundTag tag = buf.readNbt();
                        WTMenuHost host = locator.locate(client.player, WTMenuHost.class);
                        if (host != null)
                            host.getItemStack().setTag(tag);
                        buf.release();
                        CraftingTerminalHandler.getCraftingTerminalHandler(client.player).invalidateCache();
                    });
                });
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "restock_amounts"),
                (client, handler, buf, responseSender) -> {
                    buf.retain();
                    client.execute(() -> {
                        if (client.player == null)
                            return;
                        CraftingTerminalHandler ctHandler = CraftingTerminalHandler
                                .getCraftingTerminalHandler(client.player);
                        HashMap<Item, Long> items = new HashMap<>();
                        while (buf.isReadable())
                            items.put(buf.readItem().getItem(), buf.readLong());
                        ctHandler.setRestockAbleItems(items);
                        buf.release();
                    });
                });
    }
}
