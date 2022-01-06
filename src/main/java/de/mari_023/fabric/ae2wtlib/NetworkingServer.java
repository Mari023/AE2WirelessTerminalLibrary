package de.mari_023.fabric.ae2wtlib;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class NetworkingServer {
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "cycle_terminal"), (server, player, handler, buf, sender) -> server.execute(() -> {
            final AbstractContainerMenu screenHandler = player.containerMenu;

            if(!(screenHandler instanceof AEBaseMenu)) return;

            final MenuLocator locator = ((AEBaseMenu) screenHandler).getLocator();
            WTMenuHost host = locator.locate(player, WTMenuHost.class);
            if(host == null) return;
            ItemStack item = host.getItemStack();

            if(!(item.getItem() instanceof ItemWUT)) return;

            WUTHandler.cycle(player, locator, item);

            WUTHandler.open(player, locator);
        }));
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "hotkey"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String terminalName = buf.readUtf(32767);
                if(terminalName.equalsIgnoreCase("toggleRestock")) {
                    CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
                    ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
                    WUTHandler.updateClientTerminal(player, craftingTerminalHandler.getLocator(), terminal.getTag());

                    if(ItemWT.getBoolean(terminal, "restock"))
                        player.displayClientMessage(TextConstants.RESTOCK_ON, true);
                    else
                        player.displayClientMessage(TextConstants.RESTOCK_OFF, true);
                } else if(terminalName.equalsIgnoreCase("toggleMagnet")) {
                    ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    MagnetSettings settings = MagnetHandler.getMagnetSettings(terminal);
                    switch(settings.magnetMode) {
                        case OFF -> {
                            player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_INVENTORY, true);
                            settings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                        }
                        case PICKUP_INVENTORY -> {
                            player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_ME, true);
                            settings.magnetMode = MagnetMode.PICKUP_ME;
                        }
                        case PICKUP_ME -> {
                            player.displayClientMessage(TextConstants.HOTKEY_MAGNETCARD_OFF, true);
                            settings.magnetMode = MagnetMode.OFF;
                        }
                    }
                    MagnetHandler.saveMagnetSettings(terminal, settings);
                } else {
                    MenuLocator locator = WUTHandler.findTerminal(player, terminalName);

                    if(locator == null) {
                        buf.release();
                        return;
                    }

                    ItemStack terminal = WUTHandler.getItemStackFromLocator(player, locator);
                    WUTHandler.setCurrentTerminal(player, locator, terminal, terminalName);
                    WUTHandler.wirelessTerminals.get(terminalName).item().tryOpen(player, locator, terminal);
                }
                buf.release();
            });
        });
    }
}
