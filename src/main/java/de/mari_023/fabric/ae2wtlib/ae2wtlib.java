package de.mari_023.fabric.ae2wtlib;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.features.ChargerRegistry;
import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.fabric.ae2wtlib.terminal.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.WCTGuiObject;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wit.WITGuiObject;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wpt.WPTGuiObject;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import de.mari_023.fabric.ae2wtlib.wut.WUTHandler;
import de.mari_023.fabric.ae2wtlib.wut.recipe.CombineSerializer;
import de.mari_023.fabric.ae2wtlib.wut.recipe.UpgradeSerializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ae2wtlib implements IAEAddonEntrypoint {
    public static final String MOD_NAME = "ae2wtlib";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_NAME, "general"), () -> new ItemStack(ae2wtlib.CRAFTING_TERMINAL));

    @Deprecated
    public static ItemWCT CRAFTING_TERMINAL;
    public static ItemWPT PATTERN_TERMINAL;
    public static ItemWIT INTERFACE_TERMINAL;
    public static ItemWUT UNIVERSAL_TERMINAL;

    public static ItemInfinityBooster INFINITY_BOOSTER;
    public static ItemMagnetCard MAGNET_CARD;
    public static final Item CHECK_TRINKETS =new Item(new FabricItemSettings());

    @Override
    public void onAe2Initialized() {
        if(ae2wtlibConfig.INSTANCE.allowTrinket()) Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "you_need_to_enable_trinkets_to_join_this_server"), CHECK_TRINKETS);
        registerItems();

        WUTHandler.addTerminal("crafting", CRAFTING_TERMINAL::tryOpen, WCTGuiObject::new);
        WUTHandler.addTerminal("pattern", PATTERN_TERMINAL::tryOpen, WPTGuiObject::new);
        WUTHandler.addTerminal("interface", INTERFACE_TERMINAL::tryOpen, WITGuiObject::new);

        Registry.register(Registry.RECIPE_SERIALIZER, CombineSerializer.ID, CombineSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, UpgradeSerializer.ID, UpgradeSerializer.INSTANCE);

        NetworkingServer.registerServer();
        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }

    public void registerItems() {
        CRAFTING_TERMINAL = new ItemWCT();
        PATTERN_TERMINAL = new ItemWPT();
        INTERFACE_TERMINAL = new ItemWIT();
        UNIVERSAL_TERMINAL = new ItemWUT();
        INFINITY_BOOSTER = new ItemInfinityBooster();
        MAGNET_CARD = new ItemMagnetCard();

        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "infinity_booster_card"), INFINITY_BOOSTER);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "magnet_card"), MAGNET_CARD);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_crafting_terminal"), CRAFTING_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_pattern_terminal"), PATTERN_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_interface_terminal"), INTERFACE_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_NAME, "wireless_universal_terminal"), UNIVERSAL_TERMINAL);

        GridLinkables.register(CRAFTING_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(INTERFACE_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);

        ChargerRegistry.setChargeRate(CRAFTING_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(PATTERN_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(INTERFACE_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(UNIVERSAL_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate() * ae2wtlibConfig.INSTANCE.WUTChargeRateMultiplier());
    }
}