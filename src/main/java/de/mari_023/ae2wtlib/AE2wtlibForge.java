package de.mari_023.ae2wtlib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import appeng.menu.locator.MenuLocators;

import de.mari_023.ae2wtlib.curio.CurioLocator;
import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.networking.CycleTerminalPacket;
import de.mari_023.ae2wtlib.networking.RestockAmountPacket;
import de.mari_023.ae2wtlib.networking.UpdateRestockPacket;
import de.mari_023.ae2wtlib.networking.UpdateWUTPackage;

@Mod(AE2wtlib.MOD_NAME)
@Mod.EventBusSubscriber
public class AE2wtlibForge {
    private static boolean RAN_INIT = false;

    public AE2wtlibForge(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AE2wtlibConfig.SPEC, AE2wtlib.MOD_NAME + ".toml");
        MenuLocators.register(CurioLocator.class, CurioLocator::writeToPacket, CurioLocator::readFromPacket);
        modEventBus.addListener((RegisterEvent e) -> {
            if (RAN_INIT)
                return;
            RAN_INIT = true;
            AE2wtlib.registerMenus();
            AE2wtlib.createItems();
            AE2wtlib.onAe2Initialized();
            AE2WTLibCreativeTab.init();
        });
        modEventBus.addListener((BuildCreativeModeTabContentsEvent e) -> AE2wtlib.addToCreativeTab());
        modEventBus.addListener((RegisterPayloadHandlerEvent event) -> {
            IPayloadRegistrar registrar = event.registrar(AE2wtlib.MOD_NAME);
            registerPacket(registrar, CycleTerminalPacket.ID, CycleTerminalPacket::new);
            registerPacket(registrar, UpdateWUTPackage.ID, UpdateWUTPackage::new);
            registerPacket(registrar, UpdateRestockPacket.ID, UpdateRestockPacket::new);
            registerPacket(registrar, RestockAmountPacket.ID, RestockAmountPacket::new);
        });
        modEventBus.addListener((FMLClientSetupEvent e) -> AE2wtlib.registerScreens());
    }

    private static void registerPacket(IPayloadRegistrar registrar, ResourceLocation id,
            FriendlyByteBuf.Reader<AE2wtlibPacket> reader) {
        registrar.play(id, reader,
                (packet, context) -> context.workHandler().submitAsync(() -> {
                    if (context.player().isPresent())
                        packet.processPacketData(context.player().get());
                }));
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handle(PlayerInteractEvent.EntityInteractSpecific event) {
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
