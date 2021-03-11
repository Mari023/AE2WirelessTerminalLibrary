package de.mari_023.fabric.ae2wtlib;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.core.AELog;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wct.WCTGuiObject;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wit.WITGuiObject;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wpt.WPTGuiObject;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public final class ContainerHelper<C extends AEBaseContainer, I> {

    private final Class<I> interfaceClass;

    private final ContainerFactory<C, I> factory;

    public ContainerHelper(ContainerFactory<C, I> factory, Class<I> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.factory = factory;
    }

    /**
     * Opens a container that is based around a single block entity. The tile entity's position is encoded in the packet
     * buffer.
     */
    public C fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf packetBuf) {
        return fromNetwork(windowId, inv, packetBuf, (accessObj, container, buffer) -> {});
    }

    /**
     * Same as {@link #open}, but allows or additional data to be read from the packet, and passed onto the container.
     */
    public C fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf packetBuf, InitialDataDeserializer<C, I> initialDataDeserializer) {
        I host = getHostFromLocator(inv.player, ContainerLocator.read(packetBuf));
        if(host != null) {
            C container = factory.create(windowId, inv, host);
            initialDataDeserializer.deserializeInitialData(host, container, packetBuf);
            return container;
        }
        return null;
    }

    public boolean open(PlayerEntity player, ContainerLocator locator) {
        return open(player, locator, (accessObj, buffer) -> {});
    }

    public boolean open(PlayerEntity player, ContainerLocator locator, InitialDataSerializer<I> initialDataSerializer) {
        if(!(player instanceof ServerPlayerEntity)) {
            return false;
        }

        I accessInterface = getHostFromLocator(player, locator);

        if(accessInterface == null) {
            return false;
        }

        player.openHandledScreen(new HandlerFactory(locator, new LiteralText("text"), accessInterface, initialDataSerializer));

        return true;
    }

    private class HandlerFactory implements ExtendedScreenHandlerFactory {

        private final ContainerLocator locator;

        private final I accessInterface;

        private final Text title;

        private final InitialDataSerializer<I> initialDataSerializer;

        public HandlerFactory(ContainerLocator locator, Text title, I accessInterface, InitialDataSerializer<I> initialDataSerializer) {
            this.locator = locator;
            this.title = title;
            this.accessInterface = accessInterface;
            this.initialDataSerializer = initialDataSerializer;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            locator.write(buf);
            initialDataSerializer.serializeInitialData(accessInterface, buf);
        }

        @Override
        public Text getDisplayName() {
            return title;
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            C c = factory.create(syncId, inv, accessInterface);
            // Set the original locator on the opened server-side container for it to more
            // easily remember how to re-open after being closed.
            c.setLocator(locator);
            return c;
        }
    }

    private I getHostFromLocator(PlayerEntity player, ContainerLocator locator) {
        if(locator.hasItemIndex()) {
            return getHostFromPlayerInventory(player, locator);
        }
        return null;
    }

    private I getHostFromPlayerInventory(PlayerEntity player, ContainerLocator locator) {

        ItemStack it = player.inventory.getStack(locator.getItemIndex());

        if(it.isEmpty()) {
            AELog.debug("Cannot open container for player %s since they no longer hold the item in slot %d", player,
                    locator.hasItemIndex());
            return null;
        }

        if(interfaceClass.isAssignableFrom(WCTGuiObject.class) && it.getItem() instanceof ItemWCT) {//TODO do something generic, I don't want to hardcode everything
            return interfaceClass.cast(new WCTGuiObject((ItemWCT) it.getItem(), it, player, locator.getItemIndex()));
        }

        if(interfaceClass.isAssignableFrom(WPTGuiObject.class) && it.getItem() instanceof ItemWPT) {
            return interfaceClass.cast(new WPTGuiObject((ItemWPT) it.getItem(), it, player, locator.getItemIndex()));
        }

        if(interfaceClass.isAssignableFrom(WITGuiObject.class) && it.getItem() instanceof ItemWIT) {
            return interfaceClass.cast(new WITGuiObject((ItemWIT) it.getItem(), it, player, locator.getItemIndex()));
        }
        return null;
    }

    @FunctionalInterface
    public interface ContainerFactory<C, I> {
        C create(int windowId, PlayerInventory playerInv, I accessObj);
    }

    /**
     * Strategy used to serialize initial data for opening the container on the client-side into the packet that is sent
     * to the client.
     */
    @FunctionalInterface
    public interface InitialDataSerializer<I> {
        void serializeInitialData(I host, PacketByteBuf buffer);
    }

    /**
     * Strategy used to deserialize initial data for opening the container on the client-side from the packet received
     * by the server.
     */
    @FunctionalInterface
    public interface InitialDataDeserializer<C, I> {
        void deserializeInitialData(I host, C container, PacketByteBuf buffer);
    }
}