package de.mari_023.ae2wtlib.wct.magnet_card;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MagnetSettings {

    public MagnetMode magnetMode;

    /**
     * loads {@link MagnetSettings} from a tag.
     *
     * @param tag tag to load the settings from. An empty tag will result in Default {@link MagnetSettings}
     */
    public MagnetSettings(@Nullable CompoundTag tag) {
        if (tag == null)
            magnetMode = MagnetMode.DEFAULT;
        else
            magnetMode = MagnetMode.fromByte(tag.getByte("magnetMode"));
    }

    /**
     * creates {@link MagnetSettings} for an empty {@link ItemStack}
     */
    public MagnetSettings() {
        magnetMode = MagnetMode.INVALID;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("magnetMode", magnetMode.getId());
        return tag;
    }

    public boolean isActive() {
        return magnetMode == MagnetMode.PICKUP_INVENTORY || magnetMode == MagnetMode.PICKUP_ME;
    }
}
