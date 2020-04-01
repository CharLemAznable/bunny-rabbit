package com.github.charlemaznable.bunny.rabbit.mapper;

import com.github.charlemaznable.core.miner.MinerConfig;
import lombok.val;
import org.n3r.diamond.client.Minerable;

import javax.annotation.Nonnull;

@MinerConfig(group = "Bunny", dataId = "default")
public interface PluginNameMapper extends Minerable {

    @Nonnull
    default String calculatePluginName(String chargingType) {
        val configKey = "Calculate-" + chargingType;
        return getString(configKey, chargingType);
    }

    @Nonnull
    default String serveSwitchPluginName(String serveType) {
        val configKey = "ServeSwitch-" + serveType;
        return getString(configKey, serveType);
    }

    @Nonnull
    default String servePluginName(String serveType) {
        val configKey = "Serve-" + serveType;
        return getString(configKey, serveType);
    }

    @Nonnull
    default String serveCallbackPluginName(String serveType) {
        val configKey = "ServeCallback-" + serveType;
        return getString(configKey, serveType);
    }
}
