package de.mari_023.ae2wtlib;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import de.mari_023.ae2wtlib.curio.CurioLocator;

import appeng.menu.locator.MenuLocators;

@Mod(AE2wtlib.MOD_NAME)
@Mod.EventBusSubscriber
public class AE2wtlibForge {
    private static boolean RAN_INIT = false;

    public AE2wtlibForge() {
        AE2wtlibConfig.init();
        if (Platform.trinketsPresent())
            MenuLocators.register(CurioLocator.class, CurioLocator::writeToPacket, CurioLocator::readFromPacket);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((RegisterEvent event) -> {
            if (RAN_INIT)
                return;
            RAN_INIT = true;
            AE2wtlib.registerMenus();
            AE2wtlib.createItems();
            AE2wtlib.onAe2Initialized();
            AE2WTLibCreativeTab.init();
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
