package de.mari_023.fabric.ae2wtlib;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.MenuLocators;
import de.mari_023.fabric.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.trinket.TrinketLocator;
import de.mari_023.fabric.ae2wtlib.wat.ItemWAT;
import de.mari_023.fabric.ae2wtlib.wat.WATMenu;
import de.mari_023.fabric.ae2wtlib.wat.WATMenuHost;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenu;
import de.mari_023.fabric.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.fabric.ae2wtlib.wet.ItemWET;
import de.mari_023.fabric.ae2wtlib.wet.WETMenu;
import de.mari_023.fabric.ae2wtlib.wet.WETMenuHost;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import de.mari_023.fabric.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.fabric.ae2wtlib.wut.recipe.UpgradeSerializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AE2wtlib implements IAEAddonEntrypoint {
    public static final String MOD_NAME = "ae2wtlib";

    public static final CreativeModeTab ITEM_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(MOD_NAME, "general"), () -> new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL));

    public static ItemWET PATTERN_ENCODING_TERMINAL;
    public static ItemWAT PATTERN_ACCESS_TERMINAL;
    public static ItemWUT UNIVERSAL_TERMINAL;

    public static Item INFINITY_BOOSTER;
    public static ItemMagnetCard MAGNET_CARD;
    public static final Item CHECK_TRINKETS = new Item(new FabricItemSettings());

    @Override
    public void onAe2Initialized() {
        if(AE2wtlibConfig.INSTANCE.allowTrinket())
            Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "you_need_to_enable_trinkets_to_join_this_server"), CHECK_TRINKETS);
        registerItems();

        MenuLocators.register(TrinketLocator.class, TrinketLocator::writeToPacket, TrinketLocator::readFromPacket);

        WUTHandler.addTerminal("crafting", ((IUniversalWirelessTerminalItem) AEItems.WIRELESS_CRAFTING_TERMINAL.asItem())::tryOpen, WCTMenuHost::new, WCTMenu.TYPE);
        WUTHandler.addTerminal("pattern_encoding", PATTERN_ENCODING_TERMINAL::tryOpen, WETMenuHost::new, WETMenu.TYPE);
        WUTHandler.addTerminal("pattern_access", PATTERN_ACCESS_TERMINAL::tryOpen, WATMenuHost::new, WATMenu.TYPE);

        addUpgrades();//TODO add an entrypoint for addons to register their terminals before this

        Registry.register(Registry.RECIPE_SERIALIZER, CombineSerializer.ID, CombineSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, UpgradeSerializer.ID, UpgradeSerializer.INSTANCE);

        NetworkingServer.registerServer();
        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }

    public void registerItems() {
        PATTERN_ENCODING_TERMINAL = new ItemWET();
        PATTERN_ACCESS_TERMINAL = new ItemWAT();
        UNIVERSAL_TERMINAL = new ItemWUT();
        INFINITY_BOOSTER = Upgrades.createUpgradeCardItem(new FabricItemSettings().tab(AE2wtlib.ITEM_GROUP).stacksTo(1));
        MAGNET_CARD = new ItemMagnetCard();

        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "infinity_booster_card"), INFINITY_BOOSTER);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "magnet_card"), MAGNET_CARD);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "wireless_pattern_encoding_terminal"), PATTERN_ENCODING_TERMINAL);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "wireless_pattern_access_terminal"), PATTERN_ACCESS_TERMINAL);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_NAME, "wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        GridLinkables.register(PATTERN_ENCODING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_ACCESS_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
    }

    private static void addUpgrades() {
        Upgrades.add(AEItems.ENERGY_CARD, UNIVERSAL_TERMINAL, WUTHandler.terminalNames.size() * 2);
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ACCESS_TERMINAL, 2);
        Upgrades.add(AEItems.ENERGY_CARD, PATTERN_ENCODING_TERMINAL, 2);

        Upgrades.add(INFINITY_BOOSTER, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, PATTERN_ENCODING_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, PATTERN_ACCESS_TERMINAL, 1);
        Upgrades.add(INFINITY_BOOSTER, UNIVERSAL_TERMINAL, 1);
    }
}