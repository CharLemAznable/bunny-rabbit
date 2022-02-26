package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.core.config.Config;
import com.github.charlemaznable.miner.MinerConfig;
import org.n3r.diamond.client.Minerable;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.lang.Condition.nullThen;

@MinerConfig(group = "Bunny", dataId = "default")
public interface PluginNameMapper extends Minerable {

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
