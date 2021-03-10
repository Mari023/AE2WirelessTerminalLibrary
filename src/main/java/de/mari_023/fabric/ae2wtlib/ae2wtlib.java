package de.mari_023.fabric.ae2wtlib;

import appeng.container.AEBaseContainer;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ae2wtlib implements ModInitializer {

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier("ae2wtlib", "general"), () -> new ItemStack(ae2wtlib.CRAFTING_TERMINAL));

    public static final ItemWCT CRAFTING_TERMINAL = new ItemWCT(new FabricItemSettings().group(ITEM_GROUP).maxCount(1));
    public static final ItemWPT PATTERN_TERMINAL = new ItemWPT(new FabricItemSettings().group(ITEM_GROUP).maxCount(1));

    public static final ItemInfinityBooster INFINITY_BOOSTER = new ItemInfinityBooster();

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "infinity_booster_card"), INFINITY_BOOSTER);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_crafting_terminal"), CRAFTING_TERMINAL);
        Registry.register(Registry.ITEM, new Identifier("ae2wtlib", "wireless_pattern_terminal"), PATTERN_TERMINAL);
        WCTContainer.TYPE = registerScreenHandler("wireless_crafting_terminal", WCTContainer::fromNetwork);
        WPTContainer.TYPE = registerScreenHandler("wireless_pattern_terminal", WPTContainer::fromNetwork);
        /*ItemComponentCallbackV2.event(UNIVERSAL_TERMINAL).register(((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new ICurio() {

        })));*/
    }

    public static <T extends AEBaseContainer> ScreenHandlerType<T> registerScreenHandler(String Identifier, ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerExtended(new Identifier("ae2wtlib", Identifier), factory);
    }
}