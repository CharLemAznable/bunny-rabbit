package com.github.charlemaznable.bunny.rabbit.config.vertx;

import com.github.charlemaznable.core.miner.MinerConfig;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyEventBusConfig {

    @MinerConfig(dataId = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();
}
