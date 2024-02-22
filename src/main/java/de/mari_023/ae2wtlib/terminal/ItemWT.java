package de.mari_023.ae2wtlib.terminal;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import appeng.api.features.Locatables;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionHost;
import appeng.core.AEConfig;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.results.ActionHostResult;
import de.mari_023.ae2wtlib.terminal.results.LongResult;
import de.mari_023.ae2wtlib.terminal.results.Status;
import de.mari_023.ae2wtlib.wut.WUTHandler;

public abstract class ItemWT extends WirelessTerminalItem implements ICurioItem {
    public ItemWT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Item.Properties().stacksTo(1));
    }

    public boolean open(final Player player, final ItemMenuHostLocator locator,
            boolean returningFromSubmenu) {
        return MenuOpener.open(getMenuType(locator, player), player, locator, returningFromSubmenu);
    }

    public boolean tryOpen(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        if (checkPreconditions(locator.locateItem(player)))
            return open(player, locator, returningFromSubmenu);
        return false;
    }

    public abstract MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player);

    protected boolean checkPreconditions(ItemStack item) {
        return !item.isEmpty()
                && (item.getItem() == this || item.getItem() == AE2wtlibItems.instance().UNIVERSAL_TERMINAL);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level w, final Player player, final InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (!player.level().isClientSide() && checkPreconditions(is)) {
            open(player, MenuLocators.forHand(player, hand), false);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, is);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, is);
    }

    @Override
    public WirelessTerminalMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator,
            @Nullable BlockHitResult hitResult) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(locator.locateItem(player)))
                .wTMenuHostFactory().create(this, player,
                        locator, (p, subMenu) -> tryOpen(player, locator, true));
    }

    public static ActionHostResult findQuantumBridge(Level level, long frequency) {
        IActionHost quantumBridge = Locatables.quantumNetworkBridges().get(level, frequency);
        if (quantumBridge == null)
            quantumBridge = Locatables.quantumNetworkBridges().get(level, -frequency);
        if (quantumBridge == null)
            return ActionHostResult.invalid(Status.BridgeNotFound);
        return ActionHostResult.valid(quantumBridge);
    }

    public static LongResult getQEFrequency(ItemStack stack, @Nullable InternalInventory inventory) {
        if (inventory == null) {
            inventory = new AppEngInternalInventory(null, 1);
            ((AppEngInternalInventory) inventory).readFromNBT(stack.getOrCreateTag(), "singularity");
        }
        final ItemStack is = inventory.getStackInSlot(0);
        if (is.isEmpty())
            return LongResult.invalid(Status.NoSingularity);
        final CompoundTag c = is.getTag();

        if (c == null)
            return LongResult.invalid(Status.GenericInvalid);
        return LongResult.valid(c.getLong("freq"));
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        inventoryTick(stack, slotContext.entity().level(), slotContext.entity(), 0, false);
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    @Deprecated
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if (!(hostItem.getItem() instanceof ItemWT))
            return ItemStack.EMPTY;
        return ItemStack.of(hostItem.getOrCreateTag().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    @Deprecated
    public static void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if (!(hostItem.getItem() instanceof ItemWT))
            return;
        CompoundTag wctTag = hostItem.getOrCreateTag();
        if (savedItem.isEmpty())
            wctTag.remove(slot);
        else
            wctTag.put(slot, savedItem.save(new CompoundTag()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if (!(hostItem.getItem() instanceof ItemWT))
            return false;
        return hostItem.getOrCreateTag().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public static void setBoolean(ItemStack hostItem, boolean b, String key) {
        if (!(hostItem.getItem() instanceof ItemWT))
            return;
        hostItem.getOrCreateTag().putBoolean(key, b);
    }
}
