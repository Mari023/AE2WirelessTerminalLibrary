package de.mari_023.fabric.ae2wtlib.wct;

import net.minecraft.nbt.CompoundTag;

public class MagnetSettings {

    public MagnetMode magnetMode;

    public MagnetSettings(CompoundTag tag) {
        if(tag == null) {
            magnetMode = MagnetMode.INVALID;
        } else {
            magnetMode = MagnetMode.fromByte(tag.getByte("magnetMode"));
        }
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("magnetMode", magnetMode.getId());
        return tag;
    }
}