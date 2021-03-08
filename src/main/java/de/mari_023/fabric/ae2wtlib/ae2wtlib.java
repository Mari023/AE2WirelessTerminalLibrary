package de.mari_023.fabric.ae2wtlib;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ae2wtlib implements ModInitializer {

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier("ae2wtlib", "general"), () -> new ItemStack(Items.STRUCTURE_VOID));

    public static final ItemWCT UNIVERSAL_TERMINAL = new ItemWCT(new FabricItemSettings().group(ITEM_GROUP));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_universal_terminal"), UNIVERSAL_TERMINAL);
        WCTContainer.TYPE = registerScreenHandler(WCTContainer::fromNetwork, WCTContainer::open);
        /*ItemComponentCallbackV2.event(UNIVERSAL_TERMINAL).register(((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new ICurio() {

        })));*/
    }

    public static <T extends AEBaseContainer> ScreenHandlerType<T> registerScreenHandler(ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory, ContainerOpener.Opener<T> opener) {
        ScreenHandlerType<T> type = ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", "wireless_universal_terminal"), factory);
        return type;
    }
}