package de.mari_023.ae2wtlib.wct;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.SupplierInternalInventory;

import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

public class WCTMenuHost extends WTMenuHost implements IViewCellStorage {
    private final SupplierInternalInventory<InternalInventory> craftingGrid;
    private final AppEngInternalInventory trash = new AppEngInternalInventory(27);
    public static final Identifier INV_TRASH = AE2wtlibAPI.id("wct_trash");

    public WCTMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory<>(
                new StackDependentSupplier<>(
                        this::getItemStack,
                        stack -> createInv(player, stack, AEComponents.CRAFTING_INV, 9)));
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(CraftingTerminalPart.INV_CRAFTING))
            return craftingGrid;
        if (id.equals(INV_TRASH))
            return trash;
        return super.getSubInventory(id);
    }
}
