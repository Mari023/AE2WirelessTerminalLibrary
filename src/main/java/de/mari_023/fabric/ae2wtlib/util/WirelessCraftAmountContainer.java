package de.mari_023.fabric.ae2wtlib.util;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.container.slot.InaccessibleSlot;
import appeng.me.helpers.PlayerSource;
import appeng.tile.inventory.AppEngInternalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class WirelessCraftAmountContainer extends AEBaseContainer {

    public static ScreenHandlerType<WirelessCraftAmountContainer> TYPE;

    private static final ContainerHelper<WirelessCraftAmountContainer, ITerminalHost> helper = new ContainerHelper<>(
            WirelessCraftAmountContainer::new, ITerminalHost.class);

    private final Slot craftingItem;
    private IAEItemStack itemToCreate;

    public WirelessCraftAmountContainer(int id, PlayerInventory ip, final ITerminalHost te) {
        super(TYPE, id, ip, te);

        craftingItem = new InaccessibleSlot(new AppEngInternalInventory(null, 1), 0, 34, 53);
        addSlot(getCraftingItem());
    }

    public static WirelessCraftAmountContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        verifyPermissions(SecurityPermissions.CRAFT, false);
    }

    public IGrid getGrid() {
        final IActionHost h = ((IActionHost) getTarget());
        return h.getActionableNode().getGrid();
    }

    public World getWorld() {
        return getPlayerInv().player.world;
    }

    public IActionSource getActionSrc() {
        return new PlayerSource(getPlayerInv().player, (IActionHost) getTarget());
    }

    public Slot getCraftingItem() {
        return craftingItem;
    }

    public IAEItemStack getItemToCraft() {
        return itemToCreate;
    }

    public void setItemToCraft(final IAEItemStack itemToCreate) {
        this.itemToCreate = itemToCreate;
    }
}