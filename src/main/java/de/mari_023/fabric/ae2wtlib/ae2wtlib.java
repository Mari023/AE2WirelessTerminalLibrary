package de.mari_023.fabric.ae2wtlib;

import appeng.api.features.ChargerRegistry;
import appeng.api.features.GridLinkables;
import de.mari_023.fabric.ae2wtlib.terminal.ItemInfinityBooster;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

        GridLinkables.register(CRAFTING_TERMINAL, ItemWT.LINKABLE_HANDLER);
        GridLinkables.register(PATTERN_TERMINAL, ItemWT.LINKABLE_HANDLER);
        GridLinkables.register(INTERFACE_TERMINAL, ItemWT.LINKABLE_HANDLER);
        GridLinkables.register(UNIVERSAL_TERMINAL, ItemWT.LINKABLE_HANDLER);
        ChargerRegistry.setChargeRate(CRAFTING_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(PATTERN_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(INTERFACE_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate());
        ChargerRegistry.setChargeRate(UNIVERSAL_TERMINAL, ae2wtlibConfig.INSTANCE.getChargeRate() * ae2wtlibConfig.INSTANCE.WUTChargeRateMultiplier());

        Registry.register(Registry.RECIPE_SERIALIZER, CombineSerializer.ID, CombineSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, UpgradeSerializer.ID, UpgradeSerializer.INSTANCE);

        NetworkingServer.registerServer();
        ServerTickEvents.START_SERVER_TICK.register(new MagnetHandler()::doMagnet);
    }
}