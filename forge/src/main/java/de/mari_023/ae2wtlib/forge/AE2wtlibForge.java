package de.mari_023.ae2wtlib.forge;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.mari_023.ae2wtlib.AE2wtlib;

@Mod(AE2wtlib.MOD_NAME)
public class AE2wtlibForge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AE2wtlib.MOD_NAME);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            AE2wtlib.MOD_NAME);

    public AE2wtlibForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPES.register(modEventBus);
        AE2wtlib.onAe2Initialized();
    }
}
