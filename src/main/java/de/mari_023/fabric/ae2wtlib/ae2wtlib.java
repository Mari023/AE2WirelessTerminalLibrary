package de.mari_023.fabric.ae2wtlib;

import appeng.api.features.ChargerRegistry;
import appeng.core.sync.packets.PatternSlotPacket;
import appeng.menu.AEBaseMenu;
import appeng.menu.MenuLocator;
import de.mari_023.fabric.ae2wtlib.terminal.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketInventoryWrapper;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketsHelper;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTGuiObject;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wit.WITGuiObject;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTGuiObject;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import de.mari_023.fabric.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.fabric.ae2wtlib.wut.recipe.UpgradeSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ae2wtlib implements ModInitializer {
    public static final String MOD_NAME = "ae2wtlib";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_NAME, "general"), () -> new ItemStack(ae2wtlib.CRAFTING_TERMINAL));

    public static final ItemWCT CRAFTING_TERMINAL = new ItemWCT();
    public static final ItemWPT PATTERN_TERMINAL = new ItemWPT();
    public static final ItemWIT INTERFACE_TERMINAL = new ItemWIT();

    public static final ItemWUT UNIVERSAL_TERMINAL = new ItemWUT();

    public static final ItemInfinityBooster INFINITY_BOOSTER = new ItemInfinityBooster();
    public static final ItemMagnetCard MAGNET_CARD = new ItemMagnetCard();

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "infinity_booster_card"), INFINITY_BOOSTER);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "magnet_card"), MAGNET_CARD);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_crafting_terminal"), CRAFTING_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_pattern_terminal"), PATTERN_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_interface_terminal"), INTERFACE_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        WUTHandler.addTerminal("crafting", CRAFTING_TERMINAL::tryOpen, WCTGuiObject::new);
        WUTHandler.addTerminal("pattern", PATTERN_TERMINAL::tryOpen, WPTGuiObject::new);
        WUTHandler.addTerminal("interface", INTERFACE_TERMINAL::tryOpen, WITGuiObject::new);

        ChargerRegistry.setChargeRate(CRAFTING_TERMINAL, Config.getChargeRate());
        ChargerRegistry.setChargeRate(PATTERN_TERMINAL, Config.getChargeRate());
        ChargerRegistry.setChargeRate(INTERFACE_TERMINAL, Config.getChargeRate());
        ChargerRegistry.setChargeRate(UNIVERSAL_TERMINAL, Config.getChargeRate() * Config.WUTChargeRateMultiplier());

        Registry.register(Registry.RECIPE_SERIALIZER, CombineSerializer.ID, CombineSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, UpgradeSerializer.ID, UpgradeSerializer.INSTANCE);

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_NAME, "general"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String Name = buf.readString(32767);
                byte value = buf.readByte();
                final ScreenHandler c = player.currentScreenHandler;
                if(Name.startsWith("PatternTerminal.") && c instanceof WPTContainer) {
                    switch(Name) {
                        case "PatternTerminal.CraftMode" -> ((WPTContainer) c).getPatternTerminal().setCraftingRecipe(value != 0);
                        case "PatternTerminal.Encode" -> ((WPTContainer) c).encode();
                        case "PatternTerminal.Clear" -> ((WPTContainer) c).clear();
                        case "PatternTerminal.Substitute" -> ((WPTContainer) c).getPatternTerminal().setSubstitution(value != 0);
                    }
                } else if(Name.startsWith("CraftingTerminal.") && c instanceof WCTContainer) {
                    if(Name.equals("CraftingTerminal.Delete")) ((WCTContainer) c).deleteTrashSlot();
                    else if(Name.equals("CraftingTerminal.SetMagnetMode")) {
                        ((WCTContainer) c).getMagnetSettings().magnetMode = MagnetMode.fromByte(value);
                        ((WCTContainer) c).saveMagnetSettings();
                    }
                }
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_NAME, "patternslotpacket"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                if(player.currentScreenHandler instanceof WPTContainer)
                    ((WPTContainer) player.currentScreenHandler).craftOrGetItem(new PatternSlotPacket(buf));
                buf.release();
            });
        });
        /*ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_NAME, "rei_recipe"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                new REIRecipePacket(buf, player);
                buf.release();
            });
        });*///TODO remove?
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_NAME, "cycle_terminal"), (server, player, handler, buf, sender) -> server.execute(() -> {
            final ScreenHandler screenHandler = player.currentScreenHandler;

            if(!(screenHandler instanceof AEBaseMenu)) return;

            final MenuLocator locator = ((AEBaseMenu) screenHandler).getLocator();
            int slot = locator.getItemIndex();
            ItemStack item;
            if(slot >= 100 && slot < 200 && Config.allowTrinket())
                item = TrinketsHelper.getTrinketsInventory(player).getStackInSlot(slot - 100);
            else item = player.getInventory().getStack(slot);

            if(!(item.getItem() instanceof ItemWUT)) return;
            WUTHandler.cycle(player, slot, item);

            WUTHandler.open(player, locator);
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(MOD_NAME, "hotkey"), (server, player, handler, buf, sender) -> {
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
                    if(Config.allowTrinket()) {
                        TrinketInventoryWrapper trinketInv = TrinketsHelper.getTrinketsInventory(player);
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

                    if(CRAFTING_TERMINAL.canOpen(terminal, player))
                        CRAFTING_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
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
                    if(Config.allowTrinket()) {
                        TrinketInventoryWrapper trinketInv = TrinketsHelper.getTrinketsInventory(player);
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

                    if(PATTERN_TERMINAL.canOpen(terminal, player))
                        PATTERN_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
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
                    if(Config.allowTrinket()) {
                        TrinketInventoryWrapper trinketInv = TrinketsHelper.getTrinketsInventory(player);
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

                    if(INTERFACE_TERMINAL.canOpen(terminal, player))
                        INTERFACE_TERMINAL.open(player, MenuLocator.forInventorySlot(slot));
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
                        player.sendMessage(new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.on").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                    else
                        player.sendMessage(new TranslatableText("gui.ae2wtlib.restock").append(new TranslatableText("gui.ae2wtlib.off").setStyle(Style.EMPTY.withColor(TextColor.parse("red")))), true);
                } else if(terminalName.equalsIgnoreCase("toggleMagnet")) {
                    ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    MagnetSettings settings = ItemMagnetCard.loadMagnetSettings(terminal);
                    switch(settings.magnetMode) {
                        case OFF -> {
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("Pickup to Inventory").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                            settings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                        }
                        case PICKUP_INVENTORY -> {
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("Pickup to ME").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                            settings.magnetMode = MagnetMode.PICKUP_ME;
                        }
                        case PICKUP_ME -> {
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.off").setStyle(Style.EMPTY.withColor(TextColor.parse("red")))), true);
                            settings.magnetMode = MagnetMode.OFF;
                        }
                    }
                    ItemMagnetCard.saveMagnetSettings(terminal, settings);
                }
                buf.release();
            });
        });

        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }
}