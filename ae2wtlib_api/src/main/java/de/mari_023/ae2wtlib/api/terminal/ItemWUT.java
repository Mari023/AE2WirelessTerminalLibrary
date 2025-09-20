package de.mari_023.ae2wtlib.api.terminal;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.api.TextConstants;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;

import javax.annotation.Nullable;

public class ItemWUT extends ItemWT {
    public ItemWUT(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        if (WTDefinition.ofOrNull(player.getItemInHand(hand)) == null) {
            if (!level.isClientSide())
                player.displayClientMessage(TextConstants.TERMINAL_EMPTY, true);
            return InteractionResult.SUCCESS;
        }
        /*
         * if(player.isShiftKeyDown()) { if(w.isClientSide()) Minecraft.getInstance().setScreen(new
         * WUTSelectScreen(player.getItemInHand(hand))); return InteractionResult.SUCCESS;
         * } else
         */
        return super.use(level, player, hand);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 800d
                * (countInstalledTerminals(stack) + 1 + getUpgrades(stack).getInstalledUpgrades(AEItems.ENERGY_CARD));
    }

    @Override
    public boolean open(final Player player, final ItemMenuHostLocator locator,
            boolean returningFromSubmenu) {
        return WUTHandler.open(player, locator, returningFromSubmenu);
    }

    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WTDefinition.of(locator.locateItem(player)).menuType();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> lines, TooltipFlag tooltipFlags) {
        lines.accept(TextConstants.UNIVERSAL);
        for (var terminal : WTDefinition.wirelessTerminals()) {
            if (stack.get(terminal.componentType()) != null)
                lines.accept(terminal.formattedName());
        }
        super.appendHoverText(stack, context, tooltipDisplay, lines, tooltipFlags);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, countInstalledTerminals(stack) * 2, this::onUpgradesChanged);
    }

    public void onUpgradesChanged(ItemStack stack, IUpgradeInventory upgrades) {
        setAEMaxPowerMultiplier(stack,
                countInstalledTerminals(stack) + Upgrades.getEnergyCardMultiplier(upgrades));
    }

    public int countInstalledTerminals(ItemStack stack) {
        int terminals = 0;
        for (var terminal : WTDefinition.wirelessTerminals()) {
            if (stack.get(terminal.componentType()) != null)
                terminals++;
        }
        return terminals;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        for (var terminal : WTDefinition.wirelessTerminals()) {
            if (itemStack.get(terminal.componentType()) == null)
                continue;
            terminal.item().inventoryTick(itemStack, level, entity, slot);
        }
    }

    public IConfigManager getConfigManager(Supplier<ItemStack> target) {// FIXME potentially reuse the config manager?
        return WTDefinition.of(target.get()).item().getConfigManager(target);
    }
}
