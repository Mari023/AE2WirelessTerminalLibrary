package de.mari_023.ae2wtlib.wct;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import appeng.menu.slot.AppEngSlot;

import de.mari_023.ae2wtlib.api.gui.Icon;

public class ArmorSlot extends AppEngSlot {
    private final Inventory playerInventory;
    private final Armor armor;

    public ArmorSlot(Inventory playerInventory, Armor armor) {
        super(new WrappedPlayerInventory(playerInventory), armor.armor.invSlot);
        this.playerInventory = playerInventory;
        this.armor = armor;
    }

    public boolean mayPlace(ItemStack stack) {
        return armor == Armor.OFFHAND
                || stack.canEquip(armor.armor.equipmentSlot(), playerInventory.player);
    }

    @Override
    public boolean mayPickup(Player player) {
        return super.mayPickup(player) && (player.isCreative()
                || !EnchantmentHelper.has(getItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE));
    }

    public Icon icon() {
        return armor.armor.background;
    }

    public enum Armor {
        FEET(new ArmorValue(EquipmentSlot.FEET, 36, Icon.EMPTY_ARMOR_SLOT_BOOTS)),
        LEGS(new ArmorValue(EquipmentSlot.LEGS, 37, Icon.EMPTY_ARMOR_SLOT_LEGGINGS)),
        CHEST(new ArmorValue(EquipmentSlot.CHEST, 38, Icon.EMPTY_ARMOR_SLOT_CHESTPLATE)),
        HEAD(new ArmorValue(EquipmentSlot.HEAD, 39, Icon.EMPTY_ARMOR_SLOT_HELMET)),
        OFFHAND(new ArmorValue(EquipmentSlot.OFFHAND, Inventory.SLOT_OFFHAND, Icon.EMPTY_ARMOR_SLOT_SHIELD));

        public final ArmorValue armor;

        Armor(ArmorValue armor) {
            this.armor = armor;
        }

        public record ArmorValue(EquipmentSlot equipmentSlot, int invSlot, Icon background) {
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
