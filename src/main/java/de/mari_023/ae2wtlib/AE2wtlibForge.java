package de.mari_023.ae2wtlib;

import java.util.HashMap;
import java.util.Objects;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import de.mari_023.ae2wtlib.curio.CurioLocator;

import appeng.menu.locator.MenuLocators;

@Mod(AE2wtlib.MOD_NAME)
@Mod.EventBusSubscriber
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
            if (event.getRegistryKey().equals(ForgeRegistries.MENU_TYPES.getRegistryKey())) {
                AE2wtlib.registerMenus();
            } else if (event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
                AE2wtlib.createItems();
                for (var entry : ITEMS.entrySet()) {
                    ForgeRegistries.ITEMS.register(entry.getKey(), entry.getValue());
                }
                AE2wtlib.onAe2Initialized();
            } else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                AE2WTLibCreativeTab.init(Objects.requireNonNull(event.getVanillaRegistry()));
            }
        });
        modEventBus.addListener((BuildCreativeModeTabContentsEvent event) -> AE2wtlib.addToCreativeTab());
    }

    @SubscribeEvent
    public static void handle(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        AE2wtlibEvents.restock(player, event.getItem(), event.getResultStack().getCount(), event::setResultStack);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handle(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled())
            return;
        var item = event.getItemStack();

        AE2wtlibEvents.restock(player, item, item.getCount(), (stack -> player.setItemInHand(event.getHand(), stack)));
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void handle(EntityItemPickupEvent event) {
        if (event.isCanceled()) {
            return;
        }

        event.setCanceled(AE2wtlibEvents.insertStackInME(event.getItem().getItem(), event.getEntity()));
    }
}
