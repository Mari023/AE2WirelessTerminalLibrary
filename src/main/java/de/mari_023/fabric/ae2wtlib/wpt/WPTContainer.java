package de.mari_023.fabric.ae2wtlib.wpt;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.items.PatternTermMenu;
import appeng.menu.slot.AppEngSlot;
import de.mari_023.fabric.ae2wtlib.terminal.FixedWTInv;
import de.mari_023.fabric.ae2wtlib.terminal.IWTInvHolder;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wut.ItemWUT;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.List;

public class WPTContainer extends PatternTermMenu implements IWTInvHolder {

    public static final ScreenHandlerType<WPTContainer> TYPE = MenuTypeBuilder.create(WPTContainer::new, WPTGuiObject.class).requirePermission(SecurityPermissions.CRAFT).build("wireless_pattern_terminal");

    private final WPTGuiObject wptGUIObject;

    public WPTContainer(int id, final PlayerInventory ip, final WPTGuiObject gui) {
        super(TYPE, id, ip, gui, false);
        wptGUIObject = gui;

        final int slotIndex =  wptGUIObject.getSlot();
        if(slotIndex < 100 && slotIndex != 40) lockPlayerInventorySlot(slotIndex);
        createPlayerInventorySlots(ip);
        addSlot(new AppEngSlot(new FixedWTInv(getPlayerInventory(), wptGUIObject.getItemStack(), this), FixedWTInv.INFINITY_BOOSTER_CARD), SlotSemantic.BIOMETRIC_CARD);

        if(isClient()) {//FIXME set craftingMode and substitute serverside
            setCraftingMode(ItemWT.getBoolean(wptGUIObject.getItemStack(), "craftingMode"));
            setSubstitute(ItemWT.getBoolean(wptGUIObject.getItemStack(), "substitute"));
            setSubstituteFluids(ItemWT.getBoolean(wptGUIObject.getItemStack(), "substitute_fluids"));
        }
    }

    public boolean isCraftingMode() {
        return wptGUIObject.isCraftingRecipe();
    }

    public void setCraftingMode(boolean craftingMode) {
        super.setCraftingMode(craftingMode);
        wptGUIObject.setCraftingRecipe(craftingMode);
    }

    public boolean isSubstitute() {
        return wptGUIObject.isSubstitution();
    }

    public void setSubstitute(boolean substitute) {
        super.setSubstitute(substitute);
        wptGUIObject.setSubstitution(substitute);
    }

    public boolean isSubstituteFluids() {
        return wptGUIObject.isFluidSubstitution();
    }

    public void setSubstituteFluids(boolean substituteFluids) {
        super.setSubstituteFluids(substituteFluids);
        wptGUIObject.setFluidSubstitution(substituteFluids);
    }

    @Override
    public IGridNode getNetworkNode() {
        return wptGUIObject.getActionableNode();
    }

    public boolean isWUT() {
        return wptGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }

    @Override
    public List<ItemStack> getViewCells() {
        return wptGUIObject.getViewCellStorage().getViewCells();
    }
}