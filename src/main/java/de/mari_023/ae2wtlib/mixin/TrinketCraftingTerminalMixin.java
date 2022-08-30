package de.mari_023.ae2wtlib.mixin;

import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = WirelessCraftingTerminalItem.class, remap = false)
public class TrinketCraftingTerminalMixin extends WirelessTerminalItem implements Trinket {

    public TrinketCraftingTerminalMixin() {
        super(null, null);
    }

    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        inventoryTick(stack, entity.getLevel(), entity, 0, false);
    }
}
