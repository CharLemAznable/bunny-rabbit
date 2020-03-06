package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.core.miner.MinerConfig;
import lombok.val;
import org.n3r.diamond.client.Minerable;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CALCULATE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_CALLBACK_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

@MinerConfig(group = "Bunny", dataId = "default")
public interface PluginNameMapper extends Minerable {

    @Nonnull
    default String calculatePluginName(String chargingType) {
        val configKey = "Calculate-" + chargingType;
        return checkNotNull(getString(configKey),
                CALCULATE_FAILED.exception(configKey + " Config Not Found"));
    }

    @Nonnull
    default String servePluginName(String serveType) {
        val configKey = "Serve-" + serveType;
        return checkNotNull(getString(configKey),
                SERVE_FAILED.exception(configKey + " Config Not Found"));
    }

    @Nonnull
    default String serveCallbackPluginName(String serveType) {
        val configKey = "ServeCallback-" + serveType;
        return checkNotNull(getString(configKey),
                SERVE_CALLBACK_FAILED.exception(configKey + " Config Not Found"));
    }
}
