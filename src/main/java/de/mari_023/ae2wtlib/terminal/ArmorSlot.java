package de.mari_023.ae2wtlib.terminal;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import appeng.menu.slot.AppEngSlot;

public class ArmorSlot extends AppEngSlot {
    private final Inventory playerInventory;
    private final Armor armor;

    public ArmorSlot(Inventory playerInventory, Armor armor) {
        super(new WrappedPlayerInventory(playerInventory), armor.invSlot);
        this.playerInventory = playerInventory;
        this.armor = armor;
    }

    @OnlyIn(Dist.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, armor.background);
    }

    public boolean mayPlace(ItemStack stack) {
        return armor == Armor.OFFHAND
                || (playerInventory.canPlaceItem(armor.invSlot, stack) && stack.getItem() instanceof ArmorItem aItem
                        && aItem.getEquipmentSlot().equals(armor.equipmentSlot));
    }

    public enum Armor {
        FEET(EquipmentSlot.FEET, 36, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS),
        LEGS(EquipmentSlot.LEGS, 37, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS),
        CHEST(EquipmentSlot.CHEST, 38, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE),
        HEAD(EquipmentSlot.HEAD, 39, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET),
        OFFHAND(EquipmentSlot.HEAD, Inventory.SLOT_OFFHAND, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);

        public final EquipmentSlot equipmentSlot;
        public final int invSlot;
        public final ResourceLocation background;

        Armor(EquipmentSlot equipmentSlot, int invSlot, ResourceLocation background) {
            this.equipmentSlot = equipmentSlot;
            this.invSlot = invSlot;
            this.background = background;
        }
    }

    public static class DisabledOffhandSlot extends ArmorSlot {
        public DisabledOffhandSlot(Inventory playerInventory) {
            super(playerInventory, Armor.OFFHAND);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
