package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.configservice.Config;
import com.github.charlemaznable.configservice.ConfigGetter;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.config.Config.getConfigImpl;
import static com.github.charlemaznable.core.lang.Condition.nullThen;

@Config(keyset = "Bunny", key = "${bunny-config:-default}")
public interface PluginNameMapper extends ConfigGetter {

    @Nonnull
    default String calculatePluginName(String serveName) {
        return getPluginName(serveName + ".Calculate");
    }

    @Nonnull
    default String serveSwitchPluginName(String serveName) {
        return getPluginName(serveName + ".Switch");
    }

    @Nonnull
    default String servePluginName(String serveName) {
        return getPluginName(serveName + ".Serve");
    }

    @Nonnull
    default String serveCallbackPluginName(String serveName) {
        return getPluginName(serveName + ".ServeCallback");
    }

    default String getPluginName(String configKey) {
        return nullThen(getString(configKey), () -> getConfigImpl().getStr(configKey, configKey));
    }
}
