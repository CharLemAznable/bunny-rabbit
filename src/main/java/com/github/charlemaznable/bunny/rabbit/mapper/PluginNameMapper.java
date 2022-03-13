package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.configservice.ConfigGetter;
import com.github.charlemaznable.configservice.apollo.ApolloConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.github.charlemaznable.core.config.Config;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.lang.Condition.nullThen;

@ApolloConfig(namespace = "Bunny", propertyName = "${bunny-config:-default}")
@DiamondConfig(group = "Bunny", dataId = "${bunny-config:-default}")
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
        return nullThen(getString(configKey), () -> Config.getStr(configKey, configKey));
    }
}
