package de.mari_023.ae2wtlib;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import de.mari_023.ae2wtlib.AE2wtlibConfig;
import me.shedaniel.autoconfig.AutoConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(AE2wtlibConfig.class, parent).get();
    }
}
