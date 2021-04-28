package de.mari_023.fabric.ae2wtlib;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.IActionHost;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.container.implementations.WirelessCraftConfirmContainer;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import appeng.core.AELog;
import appeng.core.Api;
import de.mari_023.fabric.ae2wtlib.rei.REIRecipePacket;
import de.mari_023.fabric.ae2wtlib.terminal.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.util.ContainerHelper;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.concurrent.Future;

public class ae2wtlib implements ModInitializer {

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier("ae2wtlib", "general"), () -> new ItemStack(ae2wtlib.CRAFTING_TERMINAL));

    public static final ItemWCT CRAFTING_TERMINAL = new ItemWCT();
    public static final ItemWPT PATTERN_TERMINAL = new ItemWPT();
    public static final ItemWIT INTERFACE_TERMINAL = new ItemWIT();

    public static final ItemWUT UNIVERSAL_TERMINAL = new ItemWUT();

    public static final ItemInfinityBooster INFINITY_BOOSTER = new ItemInfinityBooster();
    public static final ItemMagnetCard MAGNET_CARD = new ItemMagnetCard();

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "infinity_booster_card"), INFINITY_BOOSTER);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "magnet_card"), MAGNET_CARD);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_crafting_terminal"), CRAFTING_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_pattern_terminal"), PATTERN_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_interface_terminal"), INTERFACE_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        WCTContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_crafting_terminal"), WCTContainer::fromNetwork);
        WPTContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_pattern_terminal"), WPTContainer::fromNetwork);
        WITContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_interface_terminal"), WITContainer::fromNetwork);
        WirelessCraftingStatusContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_crafting_status"), WirelessCraftingStatusContainer::fromNetwork);
        WirelessCraftAmountContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_craft_amount"), WirelessCraftAmountContainer::fromNetwork);
        WirelessCraftConfirmContainer.TYPE = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_craft_confirm"), WirelessCraftConfirmContainer::fromNetwork);

        WUTHandler.addTerminal("crafting", CRAFTING_TERMINAL::open);
        WUTHandler.addTerminal("pattern", PATTERN_TERMINAL::open);
        WUTHandler.addTerminal("interface", INTERFACE_TERMINAL::open);

        Api.instance().registries().charger().addChargeRate(CRAFTING_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(PATTERN_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(INTERFACE_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(UNIVERSAL_TERMINAL, Config.getChargeRate() * Config.WUTChargeRateMultiplier());

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "general"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String Name = buf.readString(32767);
                byte value = buf.readByte();
                final ScreenHandler c = player.currentScreenHandler;
                if(Name.startsWith("PatternTerminal.") && c instanceof WPTContainer) {
                    switch (Name) {
                        case "PatternTerminal.CraftMode":
                            ((WPTContainer) c).getPatternTerminal().setCraftingRecipe(value != 0);
                            break;
                        case "PatternTerminal.Encode":
                            ((WPTContainer) c).encode();
                            break;
                        case "PatternTerminal.Clear":
                            ((WPTContainer) c).clear();
                            break;
                        case "PatternTerminal.Substitute":
                            ((WPTContainer) c).getPatternTerminal().setSubstitution(value != 0);
                            break;
                    }
                } else if(Name.startsWith("CraftingTerminal.") && c instanceof WCTContainer) {
                    final WCTContainer container = (WCTContainer) c;
                    if(Name.equals("CraftingTerminal.Delete")) container.deleteTrashSlot();
                    else if(Name.equals("CraftingTerminal.SetMagnetMode")) {
                        container.getMagnetSettings().magnetMode = MagnetMode.fromByte(value);
                        container.saveMagnetSettings();
                    }
                }
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "patternslotpacket"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                if(player.currentScreenHandler instanceof WPTContainer)
                    ((WPTContainer) player.currentScreenHandler).craftOrGetItem(buf);
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "switch_gui"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                Identifier id = buf.readIdentifier();
                if(!(player.currentScreenHandler instanceof AEBaseContainer)) {
                    buf.release();
                    return;
                }

                final ContainerLocator locator = ((AEBaseContainer) player.currentScreenHandler).getLocator();
                if(locator == null) {
                    buf.release();
                    return;
                }

                switch (id.getPath()) {
                    case "wireless_crafting_terminal":
                        WCTContainer.open(player, locator);
                        break;
                    case "wireless_pattern_terminal":
                        WPTContainer.open(player, locator);
                        break;
                    case "wireless_interface_terminal":
                        WITContainer.open(player, locator);
                        break;
                    case "wireless_crafting_status":
                        WirelessCraftingStatusContainer.open(player, locator);
                }
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "rei_recipe"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                new REIRecipePacket(buf, player);
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "craft_request"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                int amount = buf.readInt();
                boolean heldShift = buf.readBoolean();
                if(player.currentScreenHandler instanceof WirelessCraftAmountContainer) {
                    final WirelessCraftAmountContainer cca = (WirelessCraftAmountContainer) player.currentScreenHandler;
                    final Object target = cca.getTarget();
                    if(target instanceof IActionHost) {
                        final IActionHost ah = (IActionHost) target;
                        final IGridNode gn = ah.getActionableNode();
                        if(gn == null) return;

                        final IGrid g = gn.getGrid();
                        if(cca.getItemToCraft() == null) return;

                        cca.getItemToCraft().setStackSize(amount);

                        Future<ICraftingJob> futureJob = null;
                        try {
                            final ICraftingGrid cg = g.getCache(ICraftingGrid.class);
                            futureJob = cg.beginCraftingJob(cca.getWorld(), cca.getGrid(), cca.getActionSrc(), cca.getItemToCraft(), null);

                            final ContainerLocator locator = cca.getLocator();
                            if(locator != null) {
                                WirelessCraftConfirmContainer.open(player, locator);

                                if(player.currentScreenHandler instanceof WirelessCraftConfirmContainer) {
                                    final WirelessCraftConfirmContainer ccc = (WirelessCraftConfirmContainer) player.currentScreenHandler;
                                    ccc.setAutoStart(heldShift);
                                    ccc.setJob(futureJob);
                                    cca.sendContentUpdates();
                                }
                            }
                        } catch (final Throwable e) {
                            if(futureJob != null) futureJob.cancel(true);
                            AELog.info(e);
                        }
                    }
                    buf.release();
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "cycle_terminal"), (server, player, handler, buf, sender) -> server.execute(() -> {
            final ScreenHandler screenHandler = player.currentScreenHandler;

            if(!(screenHandler instanceof AEBaseContainer)) return;

            final ContainerLocator locator = ((AEBaseContainer) screenHandler).getLocator();
            ItemStack item = player.inventory.getStack(locator.getItemIndex());

            if(!(item.getItem() instanceof ItemWUT)) return;
            WUTHandler.cycle(item);

            WUTHandler.open(player, locator);
        }));
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "hotkey"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String terminalName = buf.readString(32767);
                if(terminalName.equalsIgnoreCase("crafting")) {
                    int slot = -1;
                    for (int i = 0; i < player.inventory.size(); i++) {
                        ItemStack terminal = player.inventory.getStack(i);
                        if(terminal.getItem() instanceof ItemWCT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "crafting"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(terminal, "crafting");
                            break;
                        }
                    }
                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    CRAFTING_TERMINAL.open(player, ContainerHelper.getContainerLocatorForSlot(slot));
                } else if(terminalName.equalsIgnoreCase("pattern")) {
                    PlayerInventory inv = player.inventory;
                    int slot = -1;
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack terminal = inv.getStack(i);
                        if(terminal.getItem() instanceof ItemWPT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "pattern"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(terminal, "pattern");
                            break;
                        }
                    }

                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    PATTERN_TERMINAL.open(player, ContainerHelper.getContainerLocatorForSlot(slot));
                } else if(terminalName.equalsIgnoreCase("interface")) {
                    PlayerInventory inv = player.inventory;
                    int slot = -1;
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack terminal = inv.getStack(i);
                        if(terminal.getItem() instanceof ItemWIT || (terminal.getItem() instanceof ItemWUT && WUTHandler.hasTerminal(terminal, "interface"))) {
                            slot = i;
                            WUTHandler.setCurrentTerminal(terminal, "interface");
                            break;
                        }
                    }

                    if(slot == -1) {
                        buf.release();
                        return;
                    }

                    INTERFACE_TERMINAL.open(player, ContainerHelper.getContainerLocatorForSlot(slot));
                } else if(terminalName.equalsIgnoreCase("toggleRestock")) {
                    CraftingTerminalHandler craftingTerminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
                    ItemStack terminal = craftingTerminalHandler.getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    ItemWT.setBoolean(terminal, !ItemWT.getBoolean(terminal, "restock"), "restock");
                } else if(terminalName.equalsIgnoreCase("toggleMagnet")) {
                    ItemStack terminal = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
                    if(terminal.isEmpty()) {
                        buf.release();
                        return;
                    }
                    MagnetSettings settings = ItemMagnetCard.loadMagnetSettings(terminal);
                    switch (settings.magnetMode) {
                        case OFF:
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("Pickup to Inventory").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                            settings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                            break;
                        case PICKUP_INVENTORY:
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("Pickup to ME").setStyle(Style.EMPTY.withColor(TextColor.parse("green")))), true);
                            settings.magnetMode = MagnetMode.PICKUP_ME;
                            break;
                        case PICKUP_ME:
                            player.sendMessage(new TranslatableText("gui.ae2wtlib.magnetcard.hotkey").append(new TranslatableText("gui.ae2wtlib.magnetcard.desc.off").setStyle(Style.EMPTY.withColor(TextColor.parse("red")))), true);
                            settings.magnetMode = MagnetMode.OFF;
                            break;
                    }
                    ItemMagnetCard.saveMagnetSettings(terminal, settings);
                }
                buf.release();
            });
        });

        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }
}