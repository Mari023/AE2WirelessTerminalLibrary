package de.mari_023.ae2wtlib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

@Mixin(ListTag.class)
public abstract class ListTagMixin {

    @Shadow
    public abstract Tag remove(int i);

    public Tag c(int index) {
        return remove(index);
    }
}
