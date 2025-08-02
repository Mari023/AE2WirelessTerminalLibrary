package de.mari_023.ae2wtlib.api;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.RegisterEvent;

import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.UpgradeHelper;

@Mod(AE2wtlibAPI.API_MOD_NAME)
public class AE2wtlibAPIEntrypoint {
    public AE2wtlibAPIEntrypoint(IEventBus modEventBus) {
        CommonHooks.markComponentClassAsValid(ItemStack.class);// TODO figure out if there is a better way
        modEventBus.addListener((RegisterEvent event) -> {
            if (!event.getRegistryKey().equals(Registries.ITEM)) {
                return;
            }
            AddTerminalEvent.run();
            UpgradeHelper.addUpgrades();
            for (var entry : AE2wtlibComponents.DR.entrySet())
                Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, entry.getKey(), entry.getValue());
        });
    }
}
