package de.mari_023.ae2wtlib.forge;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.AE2wtlibConfig;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Mod(AE2wtlib.MOD_NAME)
public class AE2wtlibForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AE2wtlib.MOD_NAME);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            AE2wtlib.MOD_NAME);

    public AE2wtlibForge() {
        AutoConfig.register(AE2wtlibConfig.class, JanksonConfigSerializer::new);
        AE2wtlibConfig.INSTANCE = AutoConfig.getConfigHolder(AE2wtlibConfig.class).getConfig();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPES.register(modEventBus);
        modEventBus.addGenericListener(MenuType.class,
                (RegistryEvent.Register<MenuType<?>> event) -> event.getRegistry().registerAll(
                        WCTMenu.TYPE,
                        WATMenu.TYPE,
                        WETMenu.TYPE));
        AE2wtlib.onAe2Initialized();
    }
}
