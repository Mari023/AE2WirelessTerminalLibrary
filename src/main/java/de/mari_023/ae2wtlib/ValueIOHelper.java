package de.mari_023.ae2wtlib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

public class ValueIOHelper {
    public static ValueInput fromComponent(Player player, ItemStack itemStack, DataComponentType<CompoundTag> component) {
        CompoundTag tag = itemStack.getOrDefault(component, new CompoundTag());
        return fromTag(player, tag);
    }

    public static ValueInput fromTag(Player player, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), tag);
    }
}
