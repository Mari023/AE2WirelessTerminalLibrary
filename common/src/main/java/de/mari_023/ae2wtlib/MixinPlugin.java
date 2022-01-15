package de.mari_023.ae2wtlib;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class MixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        AutoConfig.register(AE2wtlibConfig.class, JanksonConfigSerializer::new);
        AE2wtlibConfig.INSTANCE = AutoConfig.getConfigHolder(AE2wtlibConfig.class).getConfig();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (targetClassName.equals("de.mari_023.fabric.ae2wtlib.wct.WCTContainer")
                && mixinClassName.equals("de.mari_023.fabric.ae2wtlib.mixin.TrinketWCTContainer")) {
            return AE2wtlibConfig.INSTANCE.allowTrinket();
        }
        if (mixinClassName.equals("de.mari_023.fabric.ae2wtlib.mixin.HandledScreenMixin")) {
            return AE2wtlibConfig.INSTANCE.allowTrinket();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
