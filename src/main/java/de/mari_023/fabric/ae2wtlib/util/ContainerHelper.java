package de.mari_023.fabric.ae2wtlib.util;

import appeng.api.util.AEPartLocation;
import appeng.container.ContainerLocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Constructor;

public final class ContainerHelper {

    private ContainerHelper() {}

    /**
     * creates a {@link ContainerLocator} for any Inventory Slot since it's constructor is private and there is no static method which directly allows this
     *
     * @param slot the slot the container is in
     * @return The new {@link ContainerLocator}
     */
    public static ContainerLocator getContainerLocatorForSlot(int slot) {
        try {
            Object containerLocatorTypePLAYER_INVENTORY = null;
            Class<?> containerLocatorTypeClass = Class.forName("appeng.container.ContainerLocator$Type");
            for(Object obj : containerLocatorTypeClass.getEnumConstants()) {
                if(obj.toString().equals("PLAYER_INVENTORY")) {
                    containerLocatorTypePLAYER_INVENTORY = obj;
                    break;
                }
            }

            Constructor<ContainerLocator> constructor = ContainerLocator.class.getDeclaredConstructor(containerLocatorTypeClass, int.class, Identifier.class, BlockPos.class, AEPartLocation.class);
            constructor.setAccessible(true);
            ContainerLocator containerLocator = constructor.newInstance(containerLocatorTypePLAYER_INVENTORY, slot, null, null, null);
            constructor.setAccessible(false);
            return containerLocator;
        } catch(Exception ignored) {}
        return null;
    }
}