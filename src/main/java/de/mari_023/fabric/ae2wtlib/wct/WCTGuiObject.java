package de.mari_023.fabric.ae2wtlib.wct;

import appeng.api.features.IWirelessTerminalHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.blockentities.IViewCellStorage;
import de.mari_023.fabric.ae2wtlib.ae2wtlib;
import de.mari_023.fabric.ae2wtlib.terminal.WTGuiObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

public class WCTGuiObject extends WTGuiObject implements IPortableCell, IViewCellStorage {

    public WCTGuiObject(final IWirelessTerminalHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return WCTContainer.TYPE;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ae2wtlib.CRAFTING_TERMINAL);
    }
}