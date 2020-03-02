package com.github.charlemaznable.bunny.rabbit.config.handler;

import com.github.charlemaznable.core.miner.MinerConfig;
import org.n3r.diamond.client.Minerable;

@MinerConfig(group = "Bunny", dataId = "default")
public interface PluginNameMapper extends Minerable {

    default String pluginName(String chargingType) {
        // 获取[key=plugin-chargingType]的配置
        return getString("plugin-" + chargingType);
    }
}
