package de.mari_023.ae2wtlib.wct;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.items.contents.StackDependentSupplier;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.SupplierInternalInventory;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

public class WCTMenuHost extends WTMenuHost implements IViewCellStorage {
    private final SupplierInternalInventory<InternalInventory> craftingGrid;
    private final AppEngInternalInventory trash = new AppEngInternalInventory(this, 27);
    public static final ResourceLocation INV_TRASH = AE2wtlib.id("wct_trash");

    public WCTMenuHost(WirelessTerminalItem item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(
                        this::getItemStack,
                        stack -> createCraftingInv(player, stack)));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AEItems.WIRELESS_CRAFTING_TERMINAL);
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(CraftingTerminalPart.INV_CRAFTING))
            return craftingGrid;
        if (id.equals(INV_TRASH))
            return trash;
        return super.getSubInventory(id);
    }

    private static InternalInventory createCraftingInv(Player player, ItemStack stack) {
        var craftingGrid = new AppEngInternalInventory(new InternalInventoryHost() {
            @Override
            public void saveChangedInventory(AppEngInternalInventory inv) {
                inv.writeToNBT(stack.getOrCreateTag(), "craftingGrid");
            }

            @Override
            public boolean isClientSide() {
                return player.level().isClientSide();
            }
        }, 9);
        if (stack.getTag() != null) {
            craftingGrid.readFromNBT(stack.getTag(), "craftingGrid");
        }
        return craftingGrid;
    }
}
