package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.NullMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.interfaces.IInventorySlotAware;
import appeng.menu.me.items.ItemTerminalMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import appeng.menu.slot.DisabledSlot;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;

public class WCTContainer extends ItemTerminalMenu implements IMenuCraftingPacket, IWTInvHolder, InternalInventoryHost {

    public static final ScreenHandlerType<WCTContainer> TYPE = MenuTypeBuilder.create(WCTContainer::new, WCTGuiObject.class).requirePermission(SecurityPermissions.CRAFT).build("wireless_crafting_terminal");

    public static final String ACTION_DELETE = "delete";
    public static final String MAGNET_MODE = "magnetMode";

    private final AppEngInternalInventory crafting;
    private final CraftingMatrixSlot[] craftingSlots = new CraftingMatrixSlot[9];
    private final CraftingTermSlot outputSlot;
    private Recipe<CraftingInventory> currentRecipe;
    final FixedWTInv fixedWTInv;

    private final WCTGuiObject wctGUIObject;

    public WCTContainer(int id, final PlayerInventory ip, final WCTGuiObject gui) {
        super(TYPE, id, ip, gui, false);
        wctGUIObject = gui;

        fixedWTInv = new FixedWTInv(getPlayerInventory(), wctGUIObject.getItemStack(), this);

        final int slotIndex = ((IInventorySlotAware) wctGUIObject).getInventorySlot();
        if(slotIndex < 100 && slotIndex != 40) lockPlayerInventorySlot(slotIndex);

        crafting = new ae2wtlibInternalInventory(this, 9, "crafting", wctGUIObject.getItemStack());

        for(int i = 0; i < 9; i++)
            addSlot(craftingSlots[i] = new CraftingMatrixSlot(this, crafting, i), SlotSemantic.CRAFTING_GRID);

        addSlot(outputSlot = new CraftingTermSlot(getPlayer(), getActionSource(), powerSource, wctGUIObject, crafting, crafting, this), SlotSemantic.CRAFTING_RESULT);

        createPlayerInventorySlots(ip);

        onContentChanged(crafting.toContainer());

        SlotsWithTrinket[5] = addSlot(new AppEngSlot(fixedWTInv, 3) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
            }
        }, SlotSemantic.MACHINE_INPUT);
        SlotsWithTrinket[6] = addSlot(new AppEngSlot(fixedWTInv, 2) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE);
            }
        }, SlotSemantic.MACHINE_PROCESSING);
        SlotsWithTrinket[7] = addSlot(new AppEngSlot(fixedWTInv, 1) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE);
            }
        }, SlotSemantic.MACHINE_OUTPUT);
        SlotsWithTrinket[8] = addSlot(new AppEngSlot(fixedWTInv, 0) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE);
            }
        }, SlotSemantic.MACHINE_CRAFTING_GRID);

        if(slotIndex == 40)
            SlotsWithTrinket[45] = addSlot(new DisabledSlot(fixedWTInv.toContainer(), FixedWTInv.OFF_HAND) {
                @Environment(EnvType.CLIENT)
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
                }
            }, SlotSemantic.PROCESSING_PRIMARY_RESULT);
        else SlotsWithTrinket[45] = addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.OFF_HAND) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        }, SlotSemantic.PROCESSING_PRIMARY_RESULT);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.TRASH), SlotSemantic.INSCRIBER_PLATE_BOTTOM);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.MAGNET_CARD), SlotSemantic.INSCRIBER_PLATE_TOP);//TODO fetch texture for card background

        registerClientAction(ACTION_DELETE, this::deleteTrashSlot);
        registerClientAction(MAGNET_MODE, MagnetMode.class, this::setMagnetMode);

        /*if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;//Trinkets only starting here
        updateTrinketSlots(true);*/
    }

    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(wctGUIObject.notInRange()) {
            if(isValidMenu()) {
                getPlayer().sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayer()).closeHandledScreen();
            }
        } else {
            double powerMultiplier = ae2wtlibConfig.INSTANCE.getPowerMultiplier(wctGUIObject.getRange(), wctGUIObject.isOutOfRange());
            ticks++;
            if(ticks > 10) {
                wctGUIObject.extractAEPower((powerMultiplier) * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
                ticks = 0;
            }

            if(wctGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) != 0) return;
            if(isValidMenu()) {
                getPlayer().sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayer()).closeHandledScreen();
            }
        }
        setValidMenu(false);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */

    @Override
    public void onContentChanged(Inventory inventory) {
        final NullMenu cn = new NullMenu();
        final CraftingInventory ic = new CraftingInventory(cn, 3, 3);

        for(int x = 0; x < 9; x++) ic.setStack(x, craftingSlots[x].getStack());

        if(currentRecipe == null || !currentRecipe.matches(ic, getPlayer().world)) {
            World world = getPlayer().world;
            currentRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ic, world).orElse(null);
        }

        if(currentRecipe == null) outputSlot.setStack(ItemStack.EMPTY);
        else {
            final ItemStack craftingResult = currentRecipe.craft(ic);
            outputSlot.setStack(craftingResult);
        }
    }

    public void clearCraftingGrid() {
        Preconditions.checkState(isClient());
        CraftingMatrixSlot slot = craftingSlots[0];
        InventoryActionPacket p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.id, 0L);
        NetworkHandler.instance().sendToServer(p);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public IGridNode getNetworkNode() {
        return wctGUIObject.getActionableNode();
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return crafting;
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public void deleteTrashSlot() {
        if(isClient()) sendClientAction(ACTION_DELETE);
        fixedWTInv.setItemDirect(FixedWTInv.TRASH, ItemStack.EMPTY);
    }

    private MagnetSettings magnetSettings;

    public MagnetSettings getMagnetSettings() {
        if(magnetSettings == null) return reloadMagnetSettings();
        return magnetSettings;
    }

    public void saveMagnetSettings() {
        ItemMagnetCard.saveMagnetSettings(wctGUIObject.getItemStack(), magnetSettings);
    }

    public MagnetSettings reloadMagnetSettings() {
        return magnetSettings = ItemMagnetCard.loadMagnetSettings(wctGUIObject.getItemStack());
    }

    public void setMagnetMode(MagnetMode mode) {
        if(isClient()) sendClientAction(MAGNET_MODE, mode);
        getMagnetSettings().magnetMode = mode;
        saveMagnetSettings();
    }

    public boolean isWUT() {
        return wctGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public List<ItemStack> getViewCells() {
        return wctGUIObject.getViewCellStorage().getViewCells();
    }

    public final Slot[] SlotsWithTrinket = new Slot[46];

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(InternalInventory internalInventory, int i, ItemStack itemStack, ItemStack itemStack1) {}

    //Trinkets starting here
    /*private final Map<SlotGroup, net.minecraft.util.Pair<Integer, Integer>> groupPos = new HashMap<>();
    private final Map<SlotGroup, List<net.minecraft.util.Pair<Integer, Integer>>> slotHeights = new HashMap<>();
    private final Map<SlotGroup, List<SlotType>> slotTypes = new HashMap<>();
    private final Map<SlotGroup, Integer> slotWidths = new HashMap<>();
    private int trinketSlotStart = 0;
    private int trinketSlotEnd = 0;
    private int groupCount = 0;

    public void updateTrinketSlots(boolean slotsChanged) {
        TrinketsApi.getTrinketComponent(getPlayer()).ifPresent(trinkets -> {
            if(slotsChanged) trinkets.update();
            Map<String, SlotGroup> groups = trinkets.getGroups();
            groupPos.clear();
            slots.removeIf(slot -> slot instanceof AppEngTrinketSlot);
            trinketSlotStart = 0;
            trinketSlotEnd = 0;

            int groupNum = 1; // Start at 1 because offhand exists

            for(SlotGroup group : groups.values().stream().sorted(Comparator.comparing(SlotGroup::getOrder)).toList()) {
                if(!hasSlots(trinkets, group)) continue;
                int id = group.getSlotId();
                switch(id) {
                    case 5, 6, 7, 8, 45 -> {
                        Slot slot = SlotsWithTrinket[id];
                        groupPos.put(group, new net.minecraft.util.Pair<>(slot.x, slot.y));
                    }
                    case -1 -> {
                        Slot slot = SlotsWithTrinket[45];
                        int x = slot.x - 18;
                        int y;
                        if(groupNum >= 2) {
                            x = 4 - (groupNum / 4) * 18;//FIXME these are probably on the wrong location, but I don't really care right now
                            y = 8 + (groupNum % 4) * 18;
                        } else y = slot.y;
                        groupPos.put(group, new net.minecraft.util.Pair<>(x, y));
                        groupNum++;
                    }
                }
            }
            if(groupNum > 4) groupCount = groupNum - 4;

            trinketSlotStart = slots.size();
            slotWidths.clear();
            slotHeights.clear();
            slotTypes.clear();
            for(Map.Entry<String, Map<String, TrinketInventory>> entry : trinkets.getInventory().entrySet()) {
                String groupId = entry.getKey();
                SlotGroup group = groups.get(groupId);
                int groupOffset = 1;

                if(group.getSlotId() != -1) groupOffset++;
                int width = 0;
                net.minecraft.util.Pair<Integer, Integer> pos = getGroupPos(group);
                if(pos == null) continue;
                for(Map.Entry<String, TrinketInventory> slot : entry.getValue().entrySet().stream().sorted(Comparator.comparingInt(a -> a.getValue().getSlotType().getOrder())).toList()) {
                    TrinketInventory stacks = slot.getValue();
                    if(stacks.size() == 0) continue;
                    int slotOffset = 1;
                    int x = (int) (pos.getLeft() + (groupOffset / 2) * 18 * Math.pow(-1, groupOffset));
                    slotHeights.computeIfAbsent(group, (k) -> new ArrayList<>()).add(new net.minecraft.util.Pair<>(x, stacks.size()));
                    slotTypes.computeIfAbsent(group, (k) -> new ArrayList<>()).add(stacks.getSlotType());
                    for(int i = 0; i < stacks.size(); i++) {
                        int y = (int) (pos.getRight() + (slotOffset / 2) * 18 * Math.pow(-1, slotOffset));
                        AppEngTrinketSlot ts = new AppEngTrinketSlot(new TrinketInventoryWrapper(stacks), i, group, stacks.getSlotType(), i, groupOffset == 1 && i == 0, false);
                        ((SlotMixin) ts).setX(x);
                        ((SlotMixin) ts).setY(y);
                        addSlot(ts);
                        slotOffset++;
                    }
                    groupOffset++;
                    width++;
                }
                slotWidths.put(group, width);
            }

            trinketSlotEnd = slots.size();
        });
    }

    private boolean hasSlots(TrinketComponent comp, SlotGroup group) {
        for(TrinketInventory inv : comp.getInventory().get(group.getName()).values()) {
            if(inv.size() > 0) return true;
        }
        return false;
    }


    public net.minecraft.util.Pair<Integer, Integer> getGroupPos(SlotGroup group) {
        return groupPos.get(group);
    }

    public List<net.minecraft.util.Pair<Integer, Integer>> getSlotHeights(SlotGroup group) {
        return slotHeights.get(group);
    }

    public List<SlotType> getSlotTypes(SlotGroup group) {
        return slotTypes.get(group);
    }

    public int getSlotWidth(SlotGroup group) {
        return slotWidths.get(group);
    }

    public int getGroupCount() {
        return groupCount;
    }

    @Override
    public void close(PlayerEntity player) {
        if(Config.allowTrinket() && player.world.isClient) {
            TrinketsClient.activeGroup = null;
            TrinketsClient.activeType = null;
            TrinketsClient.quickMoveGroup = null;
        }
        super.close(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        if(!Config.allowTrinket()) return super.transferSlot(player, index);

        Slot slot = slots.get(index);
        if(slot.hasStack()) {
            ItemStack stack = slot.getStack();
            if(index >= trinketSlotStart && index < trinketSlotEnd) {
                if(!insertItem(stack, 9, 45, false)) return ItemStack.EMPTY;
                else return stack;
            } else if(index >= 9 && index < 45) {
                TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                            for(int i = trinketSlotStart; i < trinketSlotEnd; i++) {
                                Slot s = slots.get(i);
                                if(!(s instanceof AppEngTrinketSlot ts) || !s.canInsert(stack)) continue;

                                SlotType type = ts.getType();
                                SlotReference ref = new SlotReference(ts.trinketInventory.trinketInventory, ts.getIndex());

                                boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

                                if(res && insertItem(stack, i, i + 1, false) && player.world.isClient) {
                                    TrinketsClient.quickMoveTimer = 20;
                                    TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots().get(type.getGroup());
                                    if(ref.index() > 0) TrinketsClient.quickMoveType = type;
                                    else TrinketsClient.quickMoveType = null;
                                }
                            }
                        }
                );
            }
        }
        return super.transferSlot(player, index);
    }*/
}