package de.mari_023.fabric.ae2wtlib;

import appeng.container.ContainerLocator;
import appeng.container.implementations.ContainerHelper;
import appeng.container.implementations.MEPortableCellContainer;
import appeng.core.AEConfig;
import appeng.core.localization.PlayerMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WUTContainer extends MEPortableCellContainer {

    public static ScreenHandlerType<WUTContainer> TYPE;

    private static final ContainerHelper<WUTContainer, WUTGuiObject> helper = new ContainerHelper<>(WUTContainer::new, WUTGuiObject.class);

    public static WUTContainer fromNetwork(int windowId, PlayerInventory inv, PacketByteBuf buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WUTGuiObject wirelessTerminalGUIObject;

    public WUTContainer(int id, final PlayerInventory ip, final WUTGuiObject gui) {
        super(TYPE, id, ip, gui);
        wirelessTerminalGUIObject = gui;
        System.out.println("WUTContainer created");//TODO remove line
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        System.out.println("sendContentUpdates called");//TODO remove line
        if(!wirelessTerminalGUIObject.rangeCheck()) {
            if(isServer() && isValidContainer()) {
                getPlayerInv().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.NIL_UUID);
            }

            setValidContainer(false);
        } else {
            double powerMultiplier = AEConfig.instance().wireless_getDrainRate(wirelessTerminalGUIObject.getRange());
            try {
                Method method = super.getClass().getDeclaredMethod("setPowerMultiplier", double.class);
                method.setAccessible(true);
                method.invoke(this, powerMultiplier);
            } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        }
    }
}