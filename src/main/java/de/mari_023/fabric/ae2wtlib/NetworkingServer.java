package de.mari_023.fabric.ae2wtlib;

import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.fabric.ae2wtlib.trinket.CombinedTrinketInventory;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wat.ItemWAT;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wet.ItemWET;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class NetworkingServer {
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "cycle_terminal"), (server, player, handler, buf, sender) -> server.execute(() -> {
            final AbstractContainerMenu screenHandler = player.containerMenu;

            if(!(screenHandler instanceof AEBaseMenu)) return;

            final MenuLocator locator = ((AEBaseMenu) screenHandler).getLocator();
            WTMenuHost host = locator.locate(player, WTMenuHost.class);
            if(host == null || host.getSlot() == null) return;
            ItemStack item = host.getItemStack();

            if(!(item.getItem() instanceof ItemWUT)) return;

            WUTHandler.cycle(player, host.getSlot(), item);//TODO use locator

            WUTHandler.open(player, locator);
        }));
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(AE2wtlib.MOD_NAME, "hotkey"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {//TODO unify this for all terminals
                String terminalName = buf.readUtf(32767);
                if(terminalName.equalsIgnoreCase("crafting")) {
                    MenuLocator locator = null;
                    ItemStack terminal = null;
                    for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        terminal = player.getInventory().getItem(i);
                        if(terminal.getItem() instanceof WirelessCraftingTerminalItem || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                            locator = MenuLocators.forInventorySlot(i);
                            WUTHandler.setCurrentTerminal(player, i, terminal, "crafting");
                            break;
                        }
                    }
                    if(AE2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof WirelessCraftingTerminalItem || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "crafting"))) {
                                locator = new TrinketLocator(i + 100);
                                WUTHandler.setCurrentTerminal(player, i + 100, trinketTerminal, "crafting");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }
                    if(locator == null) {
                        buf.release();
                        return;
                    }

                    ((IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem()).tryOpen(player, locator, terminal);
                } else if(terminalName.equalsIgnoreCase("pattern_encoding")) {
                    Inventory inv = player.getInventory();
                    MenuLocator locator = null;
                    ItemStack terminal = null;
                    for(int i = 0; i < inv.getContainerSize(); i++) {
                        terminal = inv.getItem(i);
                        if(terminal.getItem() instanceof ItemWET || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "pattern_encoding"))) {
                            locator = MenuLocators.forInventorySlot(i);
                            WUTHandler.setCurrentTerminal(player, i, terminal, "pattern_encoding");
                            break;
                        }
                    }
                    if(AE2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof ItemWET || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "pattern_encoding"))) {
                                locator = new TrinketLocator(i + 100);
                                WUTHandler.setCurrentTerminal(player, i + 100, trinketTerminal, "pattern_encoding");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }

                    if(locator == null) {
                        buf.release();
                        return;
                    }

                    AE2wtlib.PATTERN_ENCODING_TERMINAL.tryOpen(player, locator, terminal);
                } else if(terminalName.equalsIgnoreCase("pattern_access")) {
                    Inventory inv = player.getInventory();
                    MenuLocator locator = null;
                    ItemStack terminal = null;
                    for(int i = 0; i < inv.getContainerSize(); i++) {
                        terminal = inv.getItem(i);
                        if(terminal.getItem() instanceof ItemWAT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "pattern_access"))) {
                            locator = MenuLocators.forInventorySlot(i);
                            WUTHandler.setCurrentTerminal(player, i, terminal, "pattern_access");
                            break;
                        }
                    }
                    if(AE2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof ItemWAT || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "pattern_access"))) {
                                locator = new TrinketLocator(i + 100);
                                WUTHandler.setCurrentTerminal(player, i + 100, trinketTerminal, "pattern_access");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }

                    if(locator == null) {
                        buf.release();
                        return;
                    }

                    AE2wtlib.PATTERN_ACCESS_TERMINAL.tryOpen(player, locator, terminal);
                } else if(terminalName.equalsIgnoreCase("toggleRestock")) {
                    CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
                    ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
                    WUTHandler.updateClientTerminal(player, craftingTerminalHandler.getSlot(), terminal.getTag());

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
                    MagnetSettings settings = ItemMagnetCard.loadMagnetSettings(terminal);
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
                    ItemMagnetCard.saveMagnetSettings(terminal, settings);
                }
                buf.release();
            });
        });
    }
}
