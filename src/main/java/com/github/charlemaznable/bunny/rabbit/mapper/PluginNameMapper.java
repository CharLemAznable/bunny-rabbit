package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.core.config.Config;
import com.github.charlemaznable.core.miner.MinerConfig;
import lombok.val;
import org.n3r.diamond.client.Minerable;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.lang.Condition.nullThen;

@MinerConfig(group = "Bunny", dataId = "default")
public interface PluginNameMapper extends Minerable {

    @Nonnull
    default String calculatePluginName(String chargingType) {
        val configKey = "Calculate." + chargingType;
        return getPluginName(configKey);
    }

    @Nonnull
    default String serveSwitchPluginName(String serveType) {
        val configKey = "ServeSwitch." + serveType;
        return getPluginName(configKey);
    }

    @Nonnull
    default String servePluginName(String serveType) {
        val configKey = "Serve." + serveType;
        return getPluginName(configKey);
    }

    @Nonnull
    default String serveCallbackPluginName(String serveType) {
        val configKey = "ServeCallback." + serveType;
        return getPluginName(configKey);
    }

    default String getPluginName(String configKey) {
        return nullThen(getString(configKey), () -> Config.getStr(configKey, configKey));
    }
}
