package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.api.features.Locatables;
import appeng.core.localization.PlayerMessages;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import de.mari_023.fabric.ae2wtlib.AE2wtlib;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalLong;

@Mixin(value = WirelessTerminalItem.class, remap = false)
public abstract class WirelessTerminalItemMixin extends AEBasePoweredItem {

    public WirelessTerminalItemMixin() {
        super(null, null);
    }

    @Shadow
    public abstract OptionalLong getGridKey(ItemStack item);

    @Shadow
    public abstract boolean hasPower(Player player, double amt, ItemStack is);

    /**
     * @author Mari_023
     * @reason allow the use of Wireless Universal Terminals
     */
    @Overwrite
    protected boolean checkPreconditions(ItemStack item, Player player) {
        if (item.isEmpty() || (item.getItem() != this && item.getItem() != AE2wtlib.UNIVERSAL_TERMINAL) ) {
            return false;
        }

        var level = player.getCommandSenderWorld();
        if (level.isClientSide()) {
            return false;
        }

        var key = getGridKey(item);
        if (key.isEmpty()) {
            player.sendMessage(PlayerMessages.DeviceNotLinked.get(), Util.NIL_UUID);
            return false;
        }

        var securityStation = Locatables.securityStations().get(level, key.getAsLong());
        if (securityStation == null) {
            player.sendMessage(PlayerMessages.StationCanNotBeLocated.get(), Util.NIL_UUID);
            return false;
        }

        if (!hasPower(player, 0.5, item)) {
            player.sendMessage(PlayerMessages.DeviceNotPowered.get(), Util.NIL_UUID);
            return false;
        }
        return true;
    }
}
