package de.mari_023.fabric.ae2wtlib.wit;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.LimitedFixedItemInv;
import alexiil.mc.lib.attributes.item.SingleItemSlot;
import appeng.api.config.*;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.container.AEBaseContainer;
import appeng.container.SlotSemantic;
import appeng.container.implementations.ContainerTypeBuilder;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.container.slot.AppEngSlot;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.InventoryAction;
import appeng.items.misc.EncodedPatternItem;
import appeng.parts.misc.InterfacePart;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.InterfaceTileEntity;
import appeng.util.InventoryAdaptor;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.AdaptorFixedInv;
import appeng.util.inv.WrapperCursorItemHandler;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

public class WITContainer extends AEBaseContainer implements IWTInvHolder {

    public static ScreenHandlerType<WITContainer> TYPE = ContainerTypeBuilder.create(WITContainer::new, WITGuiObject.class).requirePermission(SecurityPermissions.BUILD).build("wireless_interface_terminal");

    private final WITGuiObject witGUIObject;
    private static long autoBase = Long.MIN_VALUE;
    private final Map<IInterfaceHost, WITContainer.InvTracker> diList = new HashMap<>();
    private final Map<Long, WITContainer.InvTracker> byId = new HashMap<>();
    private IGrid grid;
    private CompoundTag data = new CompoundTag();

    public WITContainer(int id, final PlayerInventory ip, final WITGuiObject anchor) {
        super(TYPE, id, ip, anchor);
        witGUIObject = anchor;

        if(isServer() && witGUIObject.getActionableNode() != null) grid = witGUIObject.getActionableNode().getGrid();

        final int slotIndex = ((IInventorySlotAware) witGUIObject).getInventorySlot();
        if(slotIndex < 100) lockPlayerInventorySlot(slotIndex);
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

        if(!witGUIObject.rangeCheck()) {
            if(isValidContainer()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
            setValidContainer(false);
        } else {
            powerMultiplier = Config.getPowerMultiplier(witGUIObject.getRange(), witGUIObject.isOutOfRange());

            if(witGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidContainer()) {
                    getPlayerInventory().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                    ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
                }
                setValidContainer(false);
            }
        }

        ticks++;
        if(ticks > 10) {
            witGUIObject.extractAEPower(powerMultiplier * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
            ticks = 0;
        }

        if(grid == null) return;

        int total = 0;
        boolean missing = false;

        final IActionHost host = getActionHost();
        if(host != null) {
            final IGridNode agn = host.getActionableNode();
            if(agn != null && agn.isActive()) {
                for(final IGridNode gn : grid.getMachines(InterfaceTileEntity.class)) {
                    if(gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if(ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO)
                            continue;

                        final WITContainer.InvTracker t = diList.get(ih);

                        if(t == null) missing = true;
                        else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if(!t.name.equals(dual.getTermName())) missing = true;
                        }

                        total++;
                    }
                }

                for(final IGridNode gn : grid.getMachines(InterfacePart.class)) {
                    if(gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if(ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO)
                            continue;

                        final WITContainer.InvTracker t = diList.get(ih);

                        if(t == null) missing = true;
                        else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if(!t.name.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }
                        total++;
                    }
                }
            }
        }

        if(total != diList.size() || missing) regenList(data);
        else {
            for(final Map.Entry<IInterfaceHost, WITContainer.InvTracker> en : diList.entrySet()) {
                final WITContainer.InvTracker inv = en.getValue();
                for(int x = 0; x < inv.server.getSlotCount(); x++)
                    if(isDifferent(inv.server.getInvStack(x), inv.client.getInvStack(x))) addItems(data, inv, x, 1);
            }
        }

        if(data.isEmpty()) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeCompoundTag(data);
        ServerPlayNetworking.send((ServerPlayerEntity) getPlayerInventory().player, new Identifier("ae2wtlib", "interface_terminal"), buf);
        data = new CompoundTag();
    }

    @Override
    public void doAction(final ServerPlayerEntity player, final InventoryAction action, final int slot, final long id) {
        final WITContainer.InvTracker inv = byId.get(id);
        if(inv == null) return;
        final ItemStack is = inv.server.getInvStack(slot);
        final boolean hasItemInHand = !player.inventory.getCursorStack().isEmpty();

        final InventoryAdaptor playerHand = new AdaptorFixedInv(new WrapperCursorItemHandler(player.inventory));

        // Create a wrapper around the targeted slot that will only allow insertions of
        // patterns
        LimitedFixedItemInv limitedSlotInv = inv.server.createLimitedFixedInv();
        limitedSlotInv.getAllRule().filterInserts(this::isValidPattern);
        SingleItemSlot theSlot = limitedSlotInv.getSlot(slot);

        switch(action) {
            case PICKUP_OR_SET_DOWN:
                if(hasItemInHand) {
                    ItemStack inSlot = theSlot.get();
                    if(inSlot.isEmpty())
                        player.inventory.setCursorStack(theSlot.insert(player.inventory.getCursorStack()));
                    else {
                        inSlot = inSlot.copy();
                        final ItemStack inHand = player.inventory.getCursorStack().copy();

                        theSlot.set(ItemStack.EMPTY);
                        player.inventory.setCursorStack(ItemStack.EMPTY);

                        player.inventory.setCursorStack(theSlot.insert(inHand.copy()));

                        if(player.inventory.getCursorStack().isEmpty()) player.inventory.setCursorStack(inSlot);
                        else {
                            player.inventory.setCursorStack(inHand);
                            theSlot.set(inSlot);
                        }
                    }
                } else theSlot.set(playerHand.addItems(theSlot.get()));
                break;

            case SPLIT_OR_PLACE_SINGLE:
                if(hasItemInHand) {
                    ItemStack extra = playerHand.removeItems(1, ItemStack.EMPTY, null);
                    if(!extra.isEmpty()) extra = theSlot.insert(extra);
                    if(!extra.isEmpty()) playerHand.addItems(extra);
                } else if(!is.isEmpty()) {
                    ItemStack extra = theSlot.extract((is.getCount() + 1) / 2);
                    if(!extra.isEmpty()) extra = playerHand.addItems(extra);
                    if(!extra.isEmpty()) theSlot.insert(extra);
                }
                break;

            case SHIFT_CLICK:
                final InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(player);
                theSlot.set(playerInv.addItems(theSlot.get()));
                break;

            case MOVE_REGION:
                final InventoryAdaptor playerInvAd = InventoryAdaptor.getAdaptor(player);
                for(int x = 0; x < inv.server.getSlotCount(); x++)
                    ItemHandlerUtil.setStackInSlot(inv.server, x, playerInvAd.addItems(inv.server.getInvStack(x)));
                break;

            case CREATIVE_DUPLICATE:
                if(player.isCreative() && !hasItemInHand)
                    player.inventory.setCursorStack(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                break;

            default:
                return;
        }
        updateHeld(player);
    }

    private boolean isValidPattern(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof EncodedPatternItem;
    }

    private void regenList(final CompoundTag data) {
        byId.clear();
        diList.clear();

        final IActionHost host = getActionHost();
        if(host != null) {
            final IGridNode agn = host.getActionableNode();
            if(agn != null && agn.isActive()) {
                for(final IGridNode gn : grid.getMachines(InterfaceTileEntity.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if(gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                        diList.put(ih, new WITContainer.InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                }

                for(final IGridNode gn : grid.getMachines(InterfacePart.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if(gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                        diList.put(ih, new WITContainer.InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                }
            }
        }

        data.putBoolean("clear", true);

        for(final Map.Entry<IInterfaceHost, WITContainer.InvTracker> en : diList.entrySet()) {
            final WITContainer.InvTracker inv = en.getValue();
            byId.put(inv.which, inv);
            addItems(data, inv, 0, inv.server.getSlotCount());
        }
    }

    private boolean isDifferent(final ItemStack a, final ItemStack b) {
        if(a.isEmpty() && b.isEmpty()) return false;
        if(a.isEmpty() || b.isEmpty()) return true;
        return !ItemStack.areEqual(a, b);
    }

    private void addItems(final CompoundTag data, final WITContainer.InvTracker inv, final int offset, final int length) {
        final String name = '=' + Long.toString(inv.which, Character.MAX_RADIX);
        final CompoundTag tag = data.getCompound(name);

        if(tag.isEmpty()) {
            tag.putLong("sortBy", inv.sortBy);
            tag.putString("un", Text.Serializer.toJson(inv.name));
        }

        for(int x = 0; x < length; x++) {
            final CompoundTag itemNBT = new CompoundTag();

            final ItemStack is = inv.server.getInvStack(x + offset);

            // "update" client side.
            ItemHandlerUtil.setStackInSlot(inv.client, x + offset, is.isEmpty() ? ItemStack.EMPTY : is.copy());

            if(!is.isEmpty()) is.toTag(itemNBT);

            tag.put(Integer.toString(x + offset), itemNBT);
        }
        data.put(name, tag);
    }

    private static class InvTracker {
        private final long sortBy;
        private final long which = autoBase++;
        private final Text name;
        private final FixedItemInv client;
        private final FixedItemInv server;

        public InvTracker(final DualityInterface dual, final FixedItemInv patterns, final Text name) {
            this.server = patterns;
            this.client = new AppEngInternalInventory(null, server.getSlotCount());
            this.name = name;
            sortBy = dual.getSortValue();
        }
    }


    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}