package de.mari_023.fabric.ae2wtlib;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.IActionHost;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.container.implementations.WirelessCraftingStatusContainer;
import appeng.core.AELog;
import appeng.core.Api;
import de.mari_023.fabric.ae2wtlib.rei.REIRecipePacket;
import de.mari_023.fabric.ae2wtlib.terminal.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import appeng.container.implementations.WirelessCraftConfirmContainer;
import de.mari_023.fabric.ae2wtlib.wct.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wit.WITContainer;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
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

        WCTContainer.TYPE = registerScreenHandler("wireless_crafting_terminal", WCTContainer::fromNetwork);
        WPTContainer.TYPE = registerScreenHandler("wireless_pattern_terminal", WPTContainer::fromNetwork);
        WITContainer.TYPE = registerScreenHandler("wireless_interface_terminal", WITContainer::fromNetwork);
        WirelessCraftingStatusContainer.TYPE = registerScreenHandler("wireless_crafting_status", WirelessCraftingStatusContainer::fromNetwork);
        WirelessCraftAmountContainer.TYPE = registerScreenHandler("wireless_craft_amount", WirelessCraftAmountContainer::fromNetwork);
        WirelessCraftConfirmContainer.TYPE = registerScreenHandler("wireless_craft_confirm", WirelessCraftConfirmContainer::fromNetwork);

        Api.instance().registries().charger().addChargeRate(CRAFTING_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(PATTERN_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(INTERFACE_TERMINAL, Config.getChargeRate());
        Api.instance().registries().charger().addChargeRate(UNIVERSAL_TERMINAL, Config.getChargeRate() * Config.WUTChargeRateMultiplier());

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "general"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                String Name = buf.readString();
                byte value = buf.readByte();
                final ScreenHandler c = player.currentScreenHandler;
                if(Name.startsWith("PatternTerminal.") && c instanceof WPTContainer) {
                    final WPTContainer cpt = (WPTContainer) c;
                    switch(Name) {
                        case "PatternTerminal.CraftMode":
                            cpt.getPatternTerminal().setCraftingRecipe(value != 0);
                            break;
                        case "PatternTerminal.Encode":
                            cpt.encode();
                            break;
                        case "PatternTerminal.Clear":
                            cpt.clear();
                            break;
                        case "PatternTerminal.Substitute":
                            cpt.getPatternTerminal().setSubstitution(value != 0);
                            break;
                    }
                } else if(Name.startsWith("CraftingTerminal.") && c instanceof WCTContainer) {
                    final WCTContainer cpt = (WCTContainer) c;
                    if(Name.equals("CraftingTerminal.Delete")) {
                        cpt.deleteTrashSlot();
                    }
                }
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "patternslotpacket"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                if(player.currentScreenHandler instanceof WPTContainer) {
                    final WPTContainer patternTerminal = (WPTContainer) player.currentScreenHandler;
                    patternTerminal.craftOrGetItem(buf);
                }
                buf.release();
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "switch_gui"), (server, player, handler, buf, sender) -> {
            buf.retain();
            server.execute(() -> {
                Identifier id = buf.readIdentifier();
                final ScreenHandler c = player.currentScreenHandler;
                if(!(c instanceof AEBaseContainer)) return;
                AEBaseContainer container = (AEBaseContainer) c;
                final ContainerLocator locator = container.getLocator();
                if(locator == null) return;
                switch(id.getPath()) {
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
                        if(gn == null) {
                            return;
                        }

                        final IGrid g = gn.getGrid();
                        if(cca.getItemToCraft() == null) {
                            return;
                        }

                        cca.getItemToCraft().setStackSize(amount);

                        Future<ICraftingJob> futureJob = null;
                        try {
                            final ICraftingGrid cg = g.getCache(ICraftingGrid.class);
                            futureJob = cg.beginCraftingJob(cca.getWorld(), cca.getGrid(), cca.getActionSrc(),
                                    cca.getItemToCraft(), null);

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
                        } catch(final Throwable e) {
                            if(futureJob != null) {
                                futureJob.cancel(true);
                            }
                            AELog.info(e);
                        }
                    }
                    buf.release();
                }
            });
        });
    }

    public static <T extends AEBaseContainer> ScreenHandlerType<T> registerScreenHandler(String Identifier, ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", Identifier), factory);
    }
}