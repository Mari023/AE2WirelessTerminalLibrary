package de.mari_023.ae2wtlib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ArrowNockEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import appeng.items.tools.powered.powersink.PoweredItemCapabilities;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.networking.*;
import de.mari_023.ae2wtlib.wct.ClientTerminalHandler;

@Mod(AE2wtlibAPI.MOD_NAME)
@EventBusSubscriber
public class AE2wtlibForge {
    public AE2wtlibForge(IEventBus modEventBus, ModContainer modContainer) {
        new AE2wtlibAPIImplementation();
        CommonHooks.markComponentClassAsValid(ItemStack.class);// TODO figure out if there is a better way
        AE2wtlib.registerMenus();
        modContainer.registerConfig(ModConfig.Type.COMMON, AE2wtlibConfig.SPEC,
                AE2wtlibAPI.MOD_NAME + ".toml");
        modEventBus.addListener((RegisterEvent e) -> {
            if (!e.getRegistryKey().equals(Registries.ITEM))
                return;
            AE2wtlibItems.init();
            AE2wtlib.onAe2Initialized();
            AE2wtlibCreativeTab.init();
        });
        modEventBus.addListener((BuildCreativeModeTabContentsEvent e) -> AE2wtlib.addToCreativeTab());
        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            PayloadRegistrar registrar = event.registrar(AE2wtlibAPI.MOD_NAME);
            registerPacket(registrar, CycleTerminalPacket.ID, CycleTerminalPacket.STREAM_CODEC);
            registerPacket(registrar, PickBlockPacket.ID, PickBlockPacket.STREAM_CODEC);
            registerPacket(registrar, TerminalSettingsPacket.ID, TerminalSettingsPacket.STREAM_CODEC);
            registerPacket(registrar, UpdateWUTPackage.ID, UpdateWUTPackage.STREAM_CODEC);
            registerPacket(registrar, UpdateRestockPacket.ID, UpdateRestockPacket.STREAM_CODEC);
            registerPacket(registrar, RestockAmountPacket.ID, RestockAmountPacket.STREAM_CODEC);
        });
        modEventBus.addListener(AE2wtlib::registerScreens);
        modEventBus.addListener((RegisterCapabilitiesEvent event) -> {
            registerPowerStorageItem(event, AE2wtlibItems.UNIVERSAL_TERMINAL);
            registerPowerStorageItem(event, AE2wtlibItems.PATTERN_ACCESS_TERMINAL);
            registerPowerStorageItem(event, AE2wtlibItems.PATTERN_ENCODING_TERMINAL);
        });
        AE2wtlibAdditionalComponents.init();
        AE2wtlib.ATTACHMENT_TYPES.register(modEventBus);
    }

    private static <T extends AE2wtlibPacket> void registerPacket(PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        registrar.playBidirectional(id, streamCodec,
                (packet, context) -> context.enqueueWork(() -> packet.processPacketData(context.player())));
    }

    private static void registerPowerStorageItem(RegisterCapabilitiesEvent event, ItemWT item) {
        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (object, context) -> new PoweredItemCapabilities(object, item), item);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handle(ItemEntityPickupEvent.Pre event) {
        if (event.canPickup().isFalse())
            return;
        var entity = event.getItemEntity();
        var player = event.getPlayer();
        if (event.canPickup().isDefault()) {
            if (entity.hasPickUpDelay())
                return;
            if (entity.target != null && !entity.target.equals(player.getUUID()))
                return;
        }

        AE2wtlibEvents.insertStackInME(entity, player);
    }

    @SubscribeEvent
    public static void handle(ArrowNockEvent event) {
        if (!event.hasAmmo())
            return;
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        ItemStack projectile = player.getProjectile(event.getBow());
        AE2wtlibEvents.restock(player, projectile, projectile.getCount(), (itemStack) -> {
        });
    }

    @SubscribeEvent
    public static void handle(ArrowLooseEvent event) {
        if (!event.hasAmmo())
            return;
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        ItemStack projectile = player.getProjectile(event.getBow());
        AE2wtlibEvents.restock(player, projectile, projectile.getCount(), (itemStack) -> {
        });
    }

    @SubscribeEvent
    public static void handle(ClientTickEvent.Post event) {
        ClientTerminalHandler.get().checkTerminal();
    }
}
