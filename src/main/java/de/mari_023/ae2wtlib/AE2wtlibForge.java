package de.mari_023.ae2wtlib;

import appeng.core.AppEng;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.mari_023.ae2wtlib.curio.CurioLocator;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;

import appeng.menu.locator.MenuLocators;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;

@Mod(AE2wtlib.MOD_NAME)
public class AE2wtlibForge {
    public static final HashMap<String, Item> ITEMS = new HashMap<>();

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            AE2wtlib.MOD_NAME);

    public AE2wtlibForge() {
        AE2wtlibConfig.init();
        if (Platform.trinketsPresent())
            MenuLocators.register(CurioLocator.class, CurioLocator::writeToPacket, CurioLocator::readFromPacket);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RECIPES.register(modEventBus);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registry.MENU_REGISTRY)) {
                ForgeRegistries.MENU_TYPES.register(AppEng.makeId(WCTMenu.ID), WCTMenu.TYPE);
                ForgeRegistries.MENU_TYPES.register(AppEng.makeId(WATMenu.ID), WATMenu.TYPE);
                ForgeRegistries.MENU_TYPES.register(AppEng.makeId(WETMenu.ID), WETMenu.TYPE);
                ForgeRegistries.MENU_TYPES.register(AppEng.makeId(MagnetMenu.ID), MagnetMenu.TYPE);
            } else if(event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
                AE2wtlib.createItems();
                for (var entry : ITEMS.entrySet()) {
                    ForgeRegistries.ITEMS.register(entry.getKey(), entry.getValue());
                }
                AE2wtlib.onAe2Initialized();
            }
        });
    }
}
