package de.mari_023.fabric.ae2wtlib.wit;

import appeng.api.config.*;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.core.AELog;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InterfaceTerminalPacket;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.InventoryAction;
import appeng.helpers.iface.DualityPatternProvider;
import appeng.helpers.iface.IPatternProviderHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.interfaces.IInventorySlotAware;
import appeng.menu.slot.AppEngSlot;
import appeng.parts.crafting.PatternProviderPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class WITContainer extends AEBaseMenu implements IWTInvHolder {

    public static final ScreenHandlerType<WITContainer> TYPE = MenuTypeBuilder.create(WITContainer::new, WITGuiObject.class).requirePermission(SecurityPermissions.BUILD).build("wireless_interface_terminal");
    private static long inventorySerial = Long.MIN_VALUE;

    private final WITGuiObject witGUIObject;
    private final Map<IPatternProviderHost, InvTracker> diList = new IdentityHashMap<>();
    private final Map<Long, WITContainer.InvTracker> byId = new HashMap<>();

    public WITContainer(int id, final PlayerInventory ip, final WITGuiObject anchor) {
        super(TYPE, id, ip, anchor);
        witGUIObject = anchor;

        final int slotIndex = ((IInventorySlotAware) witGUIObject).getInventorySlot();
        if(slotIndex < 100 && slotIndex != 40) lockPlayerInventorySlot(slotIndex);
        createPlayerInventorySlots(ip);

        final FixedWTInv fixedWITInv = new FixedWTInv(getPlayerInventory(), witGUIObject.getItemStack(), this);
        addSlot(new AppEngSlot(fixedWITInv, FixedWTInv.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);
    }

    private double powerMultiplier = 1;
    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(witGUIObject.notInRange()) {
            if(isValidMenu()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
            setValidMenu(false);
        } else {
            powerMultiplier = Config.getPowerMultiplier(witGUIObject.getRange(), witGUIObject.isOutOfRange());

            if(witGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidMenu()) {
                    getPlayerInventory().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                    ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
                }
                setValidMenu(false);
            }
        }

        ticks++;
        if(ticks > 10) {
            witGUIObject.extractAEPower(powerMultiplier * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
            ticks = 0;
        }

        IGrid grid = getGrid();
        VisitorState state = new VisitorState();
        if(grid != null) {
            visitInterfaceHosts(grid, PatternProviderBlockEntity.class, state);
            visitInterfaceHosts(grid, PatternProviderPart.class, state);
        }

        InterfaceTerminalPacket packet;
        if(state.total == diList.size() && !state.forceFullUpdate) packet = createIncrementalUpdate();
        else packet = createFullUpdate(grid);

        if(packet != null) NetworkHandler.instance().sendTo(packet, (ServerPlayerEntity) getPlayerInventory().player);
    }

    @Nullable
    private IGrid getGrid() {
        IActionHost host = getActionHost();
        if(host != null) {
            IGridNode agn = host.getActionableNode();
            if(agn != null && agn.isActive()) return agn.getGrid();
        }
        return null;
    }

    private <T extends IPatternProviderHost> void visitInterfaceHosts(IGrid grid, Class<T> machineClass, VisitorState state) {
        for(var ih : grid.getActiveMachines(machineClass)) {
            var dual = ih.getDuality();
            if(dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) continue;

            final InvTracker t = diList.get(ih);
            if(t == null || !t.name.equals(dual.getTermName())) state.forceFullUpdate = true;

            state.total++;
        }
    }

    public void doAction(ServerPlayerEntity player, InventoryAction action, int slot, long id) {
        InvTracker inv = byId.get(id);
        if(inv != null) {
            if(slot >= 0 && slot < inv.server.size()) {
                ItemStack is = inv.server.getStackInSlot(slot);
                FilteredInternalInventory interfaceSlot = new FilteredInternalInventory(inv.server.getSlotInv(slot), new PatternSlotFilter());
                ItemStack carried = getCursorStack();
                ItemStack inHand;
                ItemStack extra;
                switch(action) {
                    case PICKUP_OR_SET_DOWN:
                        if(!carried.isEmpty()) {
                            extra = interfaceSlot.getStackInSlot(0);
                            if(extra.isEmpty()) {
                                setCursorStack(interfaceSlot.addItems(carried));
                            } else {
                                extra = extra.copy();
                                inHand = carried.copy();
                                interfaceSlot.setItemDirect(0, ItemStack.EMPTY);
                                setCursorStack(ItemStack.EMPTY);
                                setCursorStack(interfaceSlot.addItems(inHand.copy()));
                                if(carried.isEmpty()) {
                                    setCursorStack(extra);
                                } else {
                                    setCursorStack(inHand);
                                    interfaceSlot.setItemDirect(0, extra);
                                }
                            }
                        } else {
                            setCursorStack(interfaceSlot.getStackInSlot(0));
                            interfaceSlot.setItemDirect(0, ItemStack.EMPTY);
                        }
                        break;
                    case SPLIT_OR_PLACE_SINGLE:
                        if(!carried.isEmpty()) {
                            extra = carried.split(1);
                            if(!extra.isEmpty()) {
                                extra = interfaceSlot.addItems(extra);
                            }

                            if(!extra.isEmpty()) {
                                carried.increment(extra.getCount());
                            }
                        } else if(!is.isEmpty()) {
                            setCursorStack(interfaceSlot.extractItem(0, (is.getCount() + 1) / 2, false));
                        }
                        break;
                    case SHIFT_CLICK:
                        extra = interfaceSlot.getStackInSlot(0).copy();
                        if(!player.getInventory().insertStack(extra)) {
                            interfaceSlot.setItemDirect(0, extra);
                        } else {
                            interfaceSlot.setItemDirect(0, ItemStack.EMPTY);
                        }
                        break;
                    case MOVE_REGION:
                        for(int x = 0; x < inv.server.size(); ++x) {
                            inHand = inv.server.getStackInSlot(x);
                            if(!player.getInventory().insertStack(inHand)) {
                                interfaceSlot.setItemDirect(0, inHand);
                            } else {
                                interfaceSlot.setItemDirect(0, ItemStack.EMPTY);
                            }
                        }

                        return;
                    case CREATIVE_DUPLICATE:
                        if(player.getAbilities().creativeMode && carried.isEmpty()) {
                            setCursorStack(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                        }
                }

            } else {
                AELog.warn("Client refers to invalid slot %d of inventory %s", slot, inv.name.getString());
            }
        }
    }

    private InterfaceTerminalPacket createFullUpdate(@Nullable IGrid grid) {
        byId.clear();
        diList.clear();

        if(grid == null) return new InterfaceTerminalPacket(true, new NbtCompound());

        for(var ih : grid.getActiveMachines(PatternProviderBlockEntity.class)) {
            var dual = ih.getDuality();
            if(dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                diList.put(ih, new InvTracker(dual, dual.getPatternInv(), dual.getTermName()));
        }

        for(var ih : grid.getActiveMachines(PatternProviderPart.class)) {
            var dual = ih.getDuality();
            if(dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                diList.put(ih, new InvTracker(dual, dual.getPatternInv(), dual.getTermName()));
        }

        NbtCompound data = new NbtCompound();
        for(var inv : diList.values()) {
            byId.put(inv.serverId, inv);
            addItems(data, inv, 0, inv.server.size());
        }
        return new InterfaceTerminalPacket(true, data);
    }

    private InterfaceTerminalPacket createIncrementalUpdate() {
        NbtCompound data = null;
        for(final var en : diList.entrySet()) {
            final InvTracker inv = en.getValue();
            for(int x = 0; x < inv.server.size(); x++) {
                if(isDifferent(inv.server.getStackInSlot(x), inv.client.getStackInSlot(x))) {
                    if(data == null) data = new NbtCompound();
                    addItems(data, inv, x, 1);
                }
            }
        }
        return data == null ? null : new InterfaceTerminalPacket(false, data);
    }

    private boolean isDifferent(final ItemStack a, final ItemStack b) {
        if(a.isEmpty() && b.isEmpty()) return false;
        if(a.isEmpty() || b.isEmpty()) return true;
        return !ItemStack.areEqual(a, b);
    }

    private void addItems(NbtCompound data, InvTracker inv, int offset, int length) {
        String name = "=" + Long.toString(inv.serverId, 36);
        NbtCompound tag = data.getCompound(name);
        if(tag.isEmpty()) {
            tag.putLong("sortBy", inv.sortBy);
            tag.putString("un", Text.Serializer.toJson(inv.name));
        }

        for(int x = 0; x < length; ++x) {
            NbtCompound itemNBT = new NbtCompound();
            ItemStack is = inv.server.getStackInSlot(x + offset);
            inv.client.setItemDirect(x + offset, is.isEmpty() ? ItemStack.EMPTY : is.copy());
            if(!is.isEmpty()) is.writeNbt(itemNBT);

            tag.put(Integer.toString(x + offset), itemNBT);
        }
        data.put(name, tag);
    }

    private static class VisitorState {
        int total;
        boolean forceFullUpdate;

        private VisitorState() {}
    }

    private static class InvTracker {
        private final long sortBy;
        private final long serverId = inventorySerial++;
        private final Text name;
        // This is used to track the inventory contents we sent to the client for change detection
        private final InternalInventory client;
        // This is a reference to the real inventory used by this machine
        private final InternalInventory server;

        public InvTracker(final DualityPatternProvider dual, final InternalInventory patterns, final Text name) {
            server = patterns;
            client = new AppEngInternalInventory(server.size());
            this.name = name;
            sortBy = dual.getSortValue();
        }
    }

    private static class PatternSlotFilter implements IAEItemFilter {
        private PatternSlotFilter() {}

        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return true;
        }

        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof EncodedPatternItem;
        }
    }

    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}