package de.mari_023.fabric.ae2wtlib;

import appeng.menu.AEBaseMenu;
import appeng.menu.MenuLocator;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.trinket.CombinedTrinketInventory;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class NetworkingServer {
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "cycle_terminal"), (server, player, handler, buf, sender) -> server.execute(() -> {
            final ScreenHandler screenHandler = player.currentScreenHandler;

            if(!(screenHandler instanceof AEBaseMenu)) return;

            final MenuLocator locator = ((AEBaseMenu) screenHandler).getLocator();
            int slot = locator.getItemIndex();
            ItemStack item;
            if(slot >= 100 && slot < 200 && ae2wtlibConfig.INSTANCE.allowTrinket())
                item = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(slot - 100);
            else item = player.getInventory().getStack(slot);

            if(!(item.getItem() instanceof ItemWUT)) return;
            WUTHandler.cycle(player, slot, item);

            WUTHandler.open(player, locator);
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(ae2wtlib.MOD_NAME, "hotkey"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String terminalName = buf.readString(32767);
                if(terminalName.equalsIgnoreCase("crafting")) {
                    int slot = -1;
                    ItemStack terminal = null;
                    for(int i = 0; i < player.getInventory().size(); i++) {
                        terminal = player.getInventory().getStack(i);
                        if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(player, slot, terminal, "crafting");
                            break;
                        }
                    }
                    if(ae2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof ItemWCT || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "crafting"))) {
                                slot = i + 100;
                                WUTHandler.setCurrentTerminal(player, slot, trinketTerminal, "crafting");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }
                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    if(ae2wtlib.CRAFTING_TERMINAL.canOpen(terminal, player))
                        ae2wtlib.CRAFTING_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
                } else if(terminalName.equalsIgnoreCase("pattern")) {
                    PlayerInventory inv = player.getInventory();
                    int slot = -1;
                    ItemStack terminal = null;
                    for(int i = 0; i < inv.size(); i++) {
                        terminal = inv.getStack(i);
                        if(terminal.getItem() instanceof ItemWPT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "pattern"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(player, slot, terminal, "pattern");
                            break;
                        }
                    }
                    if(ae2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof ItemWPT || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "pattern"))) {
                                slot = i + 100;
                                WUTHandler.setCurrentTerminal(player, slot, trinketTerminal, "pattern");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }

                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    if(ae2wtlib.PATTERN_TERMINAL.canOpen(terminal, player))
                        ae2wtlib.PATTERN_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
                } else if(terminalName.equalsIgnoreCase("interface")) {
                    PlayerInventory inv = player.getInventory();
                    int slot = -1;
                    ItemStack terminal = null;
                    for(int i = 0; i < inv.size(); i++) {
                        terminal = inv.getStack(i);
                        if(terminal.getItem() instanceof ItemWIT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "interface"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(player, slot, terminal, "interface");
                            break;
                        }
                    }
                    if(ae2wtlibConfig.INSTANCE.allowTrinket()) {
                        CombinedTrinketInventory trinketInv = TrinketsHelper.getTrinketsInventory(player);
                        for(int i = 0; i < trinketInv.size(); i++) {
                            ItemStack trinketTerminal = trinketInv.getStackInSlot(i);
                            if(trinketTerminal.getItem() instanceof ItemWIT || (trinketTerminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(trinketTerminal, "interface"))) {
                                slot = i + 100;
                                WUTHandler.setCurrentTerminal(player, slot, trinketTerminal, "interface");
                                terminal = trinketTerminal;
                                break;
                            }
                        }
                    }

                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    if(ae2wtlib.INTERFACE_TERMINAL.canOpen(terminal, player))
                        ae2wtlib.INTERFACE_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
                } else if(terminalName.equalsIgnoreCase("toggleRestock")) {
                    CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
                    ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
                    WUTHandler.updateClientTerminal(player, craftingTerminalHandler.getSlot(), terminal.getNbt());

                    if(ItemWT.getBoolean(terminal, "restock"))
                        player.sendMessage(TextConstants.RESTOCK_ON, true);
                    else
                        player.sendMessage(TextConstants.RESTOCK_OFF, true);
                } else if(terminalName.equalsIgnoreCase("toggleMagnet")) {
                    ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    MagnetSettings settings = ItemMagnetCard.loadMagnetSettings(terminal);
                    switch(settings.magnetMode) {
                        case OFF -> {
                            player.sendMessage(TextConstants.HOTKEY_MAGNETCARD_INVENTORY, true);
                            settings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                        }
                        case PICKUP_INVENTORY -> {
                            player.sendMessage(TextConstants.HOTKEY_MAGNETCARD_ME, true);
                            settings.magnetMode = MagnetMode.PICKUP_ME;
                        }
                        case PICKUP_ME -> {
                            player.sendMessage(TextConstants.HOTKEY_MAGNETCARD_OFF, true);
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
