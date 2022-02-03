package de.mari_023.ae2wtlib.jei;
import appeng.integration.modules.jei.transfer.EncodePatternTransferHandler;
import appeng.integration.modules.jei.transfer.UseCraftingRecipeTransfer;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wet.WETMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(AE2wtlib.MOD_NAME, "core");
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new UseCraftingRecipeTransfer<>(WCTMenu.class, registration.getTransferHelper()), VanillaRecipeCategoryUid.CRAFTING);
        registration.addUniversalRecipeTransferHandler(new EncodePatternTransferHandler<>(WETMenu.class, registration.getTransferHelper()));
    }
}
