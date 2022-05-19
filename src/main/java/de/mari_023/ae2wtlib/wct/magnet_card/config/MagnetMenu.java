package de.mari_023.ae2wtlib.wct.magnet_card.config;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.FakeSlot;
import appeng.util.ConfigInventory;
import appeng.util.ConfigMenuInventory;

public class MagnetMenu extends AEBaseMenu implements ISubMenu {

    public static final MenuType<MagnetMenu> TYPE = MenuTypeBuilder.create(MagnetMenu::new, WCTMenuHost.class)
            .build("magnet");

    private final WCTMenuHost host;
    private final MagnetHost magnetHost;
    private final static String TOGGLE_PICKUP_MODE = "togglepickupmode";
    private final static String TOGGLE_INSERT_MODE = "toggleinsertmode";
    private final static String COPY_UP = "copy_up";
    private final static String COPY_DOWN = "copy_down";
    private final static String SWITCH_INSERT_PICKUP = "switch";

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

    public MagnetHost getMagnetHost() {
        return magnetHost;
    }

    public void togglePickupMode() {
        if (isClientSide()) {
            sendClientAction(TOGGLE_PICKUP_MODE);
            if (Minecraft.getInstance().isLocalServer())
                return;
        }
        magnetHost.togglePickupMode();
    }

    public void toggleInsertMode() {
        if (isClientSide()) {
            sendClientAction(TOGGLE_INSERT_MODE);
            if (Minecraft.getInstance().isLocalServer())
                return;
        }
        magnetHost.toggleInsertMode();
    }

    public void copyUp() {
        if (isClientSide()) {
            sendClientAction(COPY_UP);
            if (Minecraft.getInstance().isLocalServer())
                return;
        }
        magnetHost.copyUp();
    }

    public void copyDown() {
        if (isClientSide()) {
            sendClientAction(COPY_DOWN);
            if (Minecraft.getInstance().isLocalServer())
                return;
        }
        magnetHost.copyDown();
    }

    public void switchInsertPickup() {
        if (isClientSide()) {
            sendClientAction(SWITCH_INSERT_PICKUP);
            if (Minecraft.getInstance().isLocalServer())
                return;
        }
        magnetHost.switchInsertPickup();
    }
}
