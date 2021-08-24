package de.mari_023.fabric.ae2wtlib.wct;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.ContainerLocator;
import appeng.container.ContainerNull;
import appeng.container.SlotSemantic;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.container.me.items.ItemTerminalContainer;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.container.slot.CraftingTermSlot;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.IContainerCraftingPacket;
import appeng.helpers.InventoryAction;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.inv.WrapperInvItemHandler;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import de.mari_023.fabric.ae2wtlib.ae2wtlibConfig;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ae2wtlibInternalInventory;
import de.mari_023.fabric.ae2wtlib.trinket.AppEngTrinketSlot;
import de.mari_023.fabric.ae2wtlib.trinket.FixedTrinketInv;
import de.mari_023.fabric.ae2wtlib.util.ContainerHelper;
import de.mari_023.fabric.ae2wtlib.util.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.ItemMagnetCard;
import de.mari_023.fabric.ae2wtlib.wct.magnet_card.MagnetSettings;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketSlots;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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

public class WCTContainer extends ItemTerminalContainer implements IAEAppEngInventory, IContainerCraftingPacket, IWTInvHolder {

    public static ScreenHandlerType<WCTContainer> TYPE;

    public static final ContainerHelper<WCTContainer, WCTGuiObject> helper = new ContainerHelper<>(WCTContainer::new, WCTGuiObject.class);

    public static WCTContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    private final AppEngInternalInventory crafting;
    private final CraftingMatrixSlot[] craftingSlots = new CraftingMatrixSlot[9];
    private final CraftingTermSlot outputSlot;
    private Recipe<CraftingInventory> currentRecipe;
    final FixedWTInv fixedWTInv;

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WCTGuiObject wctGUIObject;

    public WCTContainer(int id, final PlayerInventory ip, final WCTGuiObject gui) {
        super(TYPE, id, ip, gui, false);
        wctGUIObject = gui;

        fixedWTInv = new FixedWTInv(getPlayerInventory(), wctGUIObject.getItemStack(), this);

        final int slotIndex = ((IInventorySlotAware) wctGUIObject).getInventorySlot();
        if(slotIndex < 100) lockPlayerInventorySlot(slotIndex);

        crafting = new ae2wtlibInternalInventory(this, 9, "crafting", wctGUIObject.getItemStack());

        for(int i = 0; i < 9; i++)
            addSlot(craftingSlots[i] = new CraftingMatrixSlot(this, crafting, i), SlotSemantic.CRAFTING_GRID);

        addSlot(outputSlot = new CraftingTermSlot(getPlayerInventory().player, getActionSource(), powerSource, wctGUIObject, crafting, crafting, this), SlotSemantic.CRAFTING_RESULT);

        createPlayerInventorySlots(ip);

        onContentChanged(new WrapperInvItemHandler(crafting));

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

        SlotsWithTrinket[45] = addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.OFFHAND) {
            @Environment(EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        }, SlotSemantic.PROCESSING_RESULT);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.TRASH), SlotSemantic.INSCRIBER_PLATE_BOTTOM);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);
        addSlot(new AppEngSlot(fixedWTInv, FixedWTInv.MAGNET_CARD), SlotSemantic.INSCRIBER_PLATE_TOP);//TODO fetch texture for card background

        if(!ae2wtlibConfig.INSTANCE.allowTrinket()) return;//Trinkets only starting here
        FixedTrinketInv inv = new FixedTrinketInv((TrinketInventory) TrinketsApi.getTrinketsInventory(getPlayerInventory().player));
        int i = 0;
        for(TrinketSlots.SlotGroup group : TrinketSlots.slotGroups) {
            int j = 0;
            for(TrinketSlots.Slot slot : group.slots) {
                boolean locked = slotIndex - 100 == i;
                AppEngTrinketSlot ts;
                ts = new AppEngTrinketSlot(inv, i, Integer.MIN_VALUE, 8, group.getName(), slot.getName(), locked);
                if(j == 0 && !group.onReal) ts.keepVisible = true;
                addSlot(ts);
                i++;
                j++;
            }
        }
    }

    private int ticks = 0;

    @Override
    public void sendContentUpdates() {
        if(isClient()) return;
        super.sendContentUpdates();

        if(!wctGUIObject.rangeCheck()) {
            if(isValidContainer()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
        } else {
            double powerMultiplier = ae2wtlibConfig.INSTANCE.getPowerMultiplier(wctGUIObject.getRange(), wctGUIObject.isOutOfRange());
            ticks++;
            if(ticks > 10) {
                wctGUIObject.extractAEPower((powerMultiplier) * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
                ticks = 0;
            }

            if(wctGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) != 0) return;
            if(isValidContainer()) {
                getPlayerInventory().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
                ((ServerPlayerEntity) getPlayerInventory().player).closeHandledScreen();
            }
        }
        setValidContainer(false);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */

    @Override
    public void onContentChanged(Inventory inventory) {
        final ContainerNull cn = new ContainerNull();
        final CraftingInventory ic = new CraftingInventory(cn, 3, 3);

        for(int x = 0; x < 9; x++) ic.setStack(x, craftingSlots[x].getStack());

        if(currentRecipe == null || !currentRecipe.matches(ic, getPlayerInventory().player.world)) {
            World world = getPlayerInventory().player.world;
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

    protected void handleNetworkInteraction(ServerPlayerEntity player, IAEItemStack stack, InventoryAction action) {
        if(action != InventoryAction.AUTO_CRAFT) {
            super.handleNetworkInteraction(player, stack, action);
            return;
        }
        WirelessCraftAmountContainer.open(player, getLocator(), stack, 1);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void saveChanges() {}

    @Override
    public void onChangeInventory(FixedItemInv inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {}

    @Override
    public FixedItemInv getInventoryByName(String name) {
        if(name.equals("player")) return new FixedInventoryVanillaWrapper(getPlayerInventory());
        else if(name.equals("crafting")) return crafting;
        return null;
    }

    @Override
    public IGridNode getNetworkNode() {
        return wctGUIObject.getActionableNode();
    }

    @Override
    public boolean useRealItems() {
        return true;
    }

    public void deleteTrashSlot() {
        fixedWTInv.setInvStack(FixedWTInv.TRASH, ItemStack.EMPTY, Simulation.ACTION);
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
        magnetSettings = ItemMagnetCard.loadMagnetSettings(wctGUIObject.getItemStack());
        if(isClient() && screen != null) screen.resetMagnetSettings();
        return magnetSettings;
    }

    @Environment(EnvType.CLIENT)
    private WCTScreen screen;

    @Environment(EnvType.CLIENT)
    public void setScreen(WCTScreen screen) {
        this.screen = screen;
    }

    public boolean isWUT() {
        return wctGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public List<ItemStack> getViewCells() {
        return wctGUIObject.getViewCellStorage().getViewCells();
    }

    public final Slot[] SlotsWithTrinket = new Slot[46];
}