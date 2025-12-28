package de.mari_023.ae2wtlib.api.terminal;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.config.Setting;
import appeng.api.ids.AEComponents;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigManagerBuilder;
import appeng.api.util.IConfigManagerListener;
import appeng.api.util.UnsupportedSettingException;

public class AE2wtlibConfigManager implements IConfigManager {
    private static final Logger LOG = LoggerFactory.getLogger(AE2wtlibConfigManager.class);

    private final Map<Setting<?>, Enum<?>> settings = new IdentityHashMap<>();
    @Nullable
    private Map<String, String> settingsMap;
    private final IConfigManagerListener listener;

    public AE2wtlibConfigManager(IConfigManagerListener listener) {
        this.listener = listener;
    }

    @Override
    public Set<Setting<?>> getSettings() {
        return settings.keySet();
    }

    public <T extends Enum<T>> void registerSetting(Setting<T> setting, T defaultValue) {
        settings.put(setting, defaultValue);
    }

    @Override
    public <T extends Enum<T>> T getSetting(Setting<T> setting) {
        var oldValue = settings.get(setting);

        if (oldValue == null) {
            throw new UnsupportedSettingException("Setting " + setting.getName() + " is not supported.");
        }

        return setting.getEnumClass().cast(oldValue);
    }

    @Override
    public <T extends Enum<T>> void putSetting(Setting<T> setting, T newValue) {
        if (!settings.containsKey(setting)) {
            throw new UnsupportedSettingException("Setting " + setting.getName() + " is not supported.");
        }
        settings.put(setting, newValue);
        listener.onSettingChanged(this, setting);
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean readFromNBT(ValueInput input) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean importSettings(Map<String, String> settings) {
        settingsMap = settings;
        boolean anythingRead = false;
        for (var setting : this.settings.keySet()) {
            String value = settings.get(setting.getName());
            if (value != null) {
                try {
                    setting.setFromString(this, value);
                    anythingRead = true;
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed to load setting {} from value '{}': {}", setting, value, e.getMessage());
                }
            }
        }
        return anythingRead;
    }

    @Override
    public Map<String, String> exportSettings() {
        Map<String, String> result = settingsMap == null ? new HashMap<>() : new HashMap<>(settingsMap);
        for (var entry : settings.entrySet()) {
            result.put(entry.getKey().getName(), settings.get(entry.getKey()).toString());
        }
        return Map.copyOf(result);
    }

    /**
     * Get a builder for configuration manager that stores its settings in a wireless terminal. This is different from
     * AE2's ConfigManager in that it keeps around unknown values instead of deleting them
     */
    public static IConfigManagerBuilder builder(Supplier<ItemStack> stack) {
        var manager = new AE2wtlibConfigManager(
                (mgr, settingName) -> stack.get().set(AEComponents.EXPORTED_SETTINGS, mgr.exportSettings()));

        return new IConfigManagerBuilder() {
            @Override
            public <T extends Enum<T>> IConfigManagerBuilder registerSetting(Setting<T> setting, T defaultValue) {
                manager.registerSetting(setting, defaultValue);
                return this;
            }

            @Override
            public IConfigManager build() {
                manager.importSettings(stack.get().getOrDefault(AEComponents.EXPORTED_SETTINGS, Map.of()));
                return manager;
            }
        };
    }
}
