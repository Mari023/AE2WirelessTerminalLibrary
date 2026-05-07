package de.mari_023.ae2wtlib.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class StackWrapper {
    public static final StackWrapper EMPTY = new StackWrapper(ItemStack.EMPTY);
    public static final Codec<StackWrapper> CODEC = ItemStack.OPTIONAL_CODEC
            .comapFlatMap(stack -> DataResult.success(new StackWrapper(stack)), StackWrapper::toStack);
    public static final StreamCodec<RegistryFriendlyByteBuf, StackWrapper> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .map(StackWrapper::new, StackWrapper::toStack);

    private final ItemStack stack;
    private final int hashCode;

    public StackWrapper(ItemStack stack) {
        this.stack = stack;
        hashCode = ItemStack.hashItemAndComponents(stack);
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other instanceof StackWrapper stackWrapper && ItemStack.matches(stack, stackWrapper.stack);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public ItemStack toStack() {
        return stack;
    }
}
