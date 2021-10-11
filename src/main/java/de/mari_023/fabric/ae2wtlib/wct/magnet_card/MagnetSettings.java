package de.mari_023.fabric.ae2wtlib.wct.magnet_card;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class MagnetSettings {

    public MagnetMode magnetMode;

    /**
     * loads {@link MagnetSettings} from a tag.
     *
     * @param tag tag to load the settings from.
     *            An empty tag will result in Default {@link MagnetSettings}
     */
    public MagnetSettings(NbtCompound tag) {
        if(tag == null) magnetMode = MagnetMode.DEFAULT;
        else magnetMode = MagnetMode.fromByte(tag.getByte("magnetMode"));
    }

    /**
     * creates {@link MagnetSettings} for an empty {@link ItemStack}
     */
    public MagnetSettings() {
        magnetMode = MagnetMode.INVALID;
    }

    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putByte("magnetMode", magnetMode.getId());
        return tag;
    }
}