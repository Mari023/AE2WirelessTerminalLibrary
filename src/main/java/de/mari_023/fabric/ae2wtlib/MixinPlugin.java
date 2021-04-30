package de.mari_023.fabric.ae2wtlib;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private boolean trinketPresent = false;
    private boolean trinketChecked = false;

    private boolean hasTrinket() {
        if(!trinketChecked) {
            trinketPresent = FabricLoader.getInstance().isModLoaded("trinkets");
        }
        trinketChecked = true;
        return trinketPresent;
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return hasTrinket() || !targetClassName.equals("de.mari_023.fabric.ae2wtlib.terminal.ItemWT") || !mixinClassName.equals("de.mari_023.fabric.ae2wtlib.mixin.TrinketWT");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}