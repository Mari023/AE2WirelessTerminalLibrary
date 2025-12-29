package de.mari_023.ae2wtlib.wct.magnet_card;

import static de.mari_023.ae2wtlib.api.AE2wtlibAPI.id;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.guisync.ClientActionKey;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.FakeSlot;
import appeng.util.ConfigInventory;
import appeng.util.ConfigMenuInventory;

import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;

public class MagnetMenu extends AEBaseMenu implements ISubMenu {
    public static final Identifier ID = id("magnet");
    public static final MenuType<MagnetMenu> TYPE = MenuTypeBuilder.create(MagnetMenu::new, WCTMenuHost.class)
            .buildUnregistered(ID);

    private final WCTMenuHost host;
    @Nullable
    private final MagnetHost magnetHost;
    private final static ClientActionKey<Void> TOGGLE_PICKUP_MODE = new ClientActionKey<>("togglepickupmode");
    private final static ClientActionKey<Void> TOGGLE_INSERT_MODE = new ClientActionKey<>("toggleinsertmode");
    private final static ClientActionKey<Void> COPY_UP = new ClientActionKey<>("copy_up");
    private final static ClientActionKey<Void> COPY_DOWN = new ClientActionKey<>("copy_down");
    private final static ClientActionKey<Void> SWITCH_INSERT_PICKUP = new ClientActionKey<>("switch");

    public MagnetMenu(int id, Inventory playerInventory, WCTMenuHost host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;

        magnetHost = CraftingTerminalHandler.getCraftingTerminalHandler(playerInventory.player)
                .getMagnetHost();

        if (magnetHost == null)
            return;

        addConfigSlots(magnetHost.pickupConfig, AE2wtlibSlotSemantics.PICKUP_CONFIG);
        addConfigSlots(magnetHost.insertConfig, AE2wtlibSlotSemantics.INSERT_CONFIG);
        createPlayerInventorySlots(playerInventory);
        registerClientAction(TOGGLE_PICKUP_MODE, this::togglePickupMode);
        registerClientAction(TOGGLE_INSERT_MODE, this::toggleInsertMode);
        registerClientAction(COPY_UP, this::copyUp);
        registerClientAction(COPY_DOWN, this::copyDown);
        registerClientAction(SWITCH_INSERT_PICKUP, this::switchInsertPickup);
    }

    private void addConfigSlots(ConfigInventory config, SlotSemantic slotSemantic) {
        ConfigMenuInventory inv = config.createMenuWrapper();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new FakeSlot(inv, y * 9 + x), slotSemantic);
            }
        }
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }

    @Nullable
    public MagnetHost getMagnetHost() {
        return magnetHost;
    }

    public void togglePickupMode() {
        if (isClientSide()) {
            sendClientAction(TOGGLE_PICKUP_MODE);
        }
        if (magnetHost == null)
            return;
        magnetHost.togglePickupMode();
    }

    public void toggleInsertMode() {
        if (isClientSide()) {
            sendClientAction(TOGGLE_INSERT_MODE);
        }
        if (magnetHost == null)
            return;
        magnetHost.toggleInsertMode();
    }

    public void copyUp() {
        if (isClientSide()) {
            sendClientAction(COPY_UP);
        }
        if (magnetHost == null)
            return;
        magnetHost.copyUp();
    }

    public void copyDown() {
        if (isClientSide()) {
            sendClientAction(COPY_DOWN);
        }
        if (magnetHost == null)
            return;
        magnetHost.copyDown();
    }

    public void switchInsertPickup() {
        if (isClientSide()) {
            sendClientAction(SWITCH_INSERT_PICKUP);
        }
        if (magnetHost == null)
            return;
        magnetHost.switchInsertPickup();
    }
}
