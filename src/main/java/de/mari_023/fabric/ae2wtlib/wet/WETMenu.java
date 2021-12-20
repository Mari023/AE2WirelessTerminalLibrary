package de.mari_023.fabric.ae2wtlib.wet;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternTermMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.fabric.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.fabric.ae2wtlib.TextConstants;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.terminal.WTInventory;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class WETMenu extends PatternTermMenu implements IWTInvHolder {

    public static final MenuType<WETMenu> TYPE = MenuTypeBuilder.create(WETMenu::new, WETMenuHost.class).requirePermission(SecurityPermissions.CRAFT).build("wireless_pattern_encoding_terminal");

    private final WETMenuHost WETGUIObject;

    public WETMenu(int id, final Inventory ip, final WETMenuHost gui) {
        super(TYPE, id, ip, gui, true);
        WETGUIObject = gui;

        AppEngSlot infinityBoosterCardSlot = new AppEngSlot(new WTInventory(getPlayerInventory(), WETGUIObject.getItemStack(), this), WTInventory.INFINITY_BOOSTER_CARD) {
            @Override
            public List<Component> getCustomTooltip(Function<ItemStack, List<Component>> getItemTooltip, ItemStack carriedItem) {
                return TextConstants.BOOSTER_SLOT;
            }
        };
        infinityBoosterCardSlot.setIcon(RestrictedInputSlot.PlacableItemType.UPGRADES.icon);
        addSlot(infinityBoosterCardSlot, AE2wtlibSlotSemantics.INFINITY_BOOSTER_CARD);

        if(isClient()) {//FIXME set craftingMode and substitute serverside
            setCraftingMode(ItemWT.getBoolean(WETGUIObject.getItemStack(), "craftingMode"));
            setSubstitute(ItemWT.getBoolean(WETGUIObject.getItemStack(), "substitute"));
            setSubstituteFluids(ItemWT.getBoolean(WETGUIObject.getItemStack(), "substitute_fluids"));
        }
    }

    public boolean isCraftingMode() {
        return WETGUIObject.isCraftingRecipe();
    }

    public void setCraftingMode(boolean craftingMode) {
        super.setCraftingMode(craftingMode);
        WETGUIObject.setCraftingRecipe(craftingMode);
    }

    public boolean isSubstitute() {
        return WETGUIObject.isSubstitution();
    }

    public void setSubstitute(boolean substitute) {
        super.setSubstitute(substitute);
        WETGUIObject.setSubstitution(substitute);
    }

    public boolean isSubstituteFluids() {
        return WETGUIObject.isFluidSubstitution();
    }

    public void setSubstituteFluids(boolean substituteFluids) {
        super.setSubstituteFluids(substituteFluids);
        WETGUIObject.setFluidSubstitution(substituteFluids);
    }

    @Override
    public IGridNode getNetworkNode() {
        return WETGUIObject.getActionableNode();
    }

    public boolean isWUT() {
        return WETGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public List<ItemStack> getViewCells() {
        return WETGUIObject.getViewCellStorage().getViewCells();
    }
}