package com.github.charlemaznable.bunny.rabbit.config.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusInterceptor;
import com.github.charlemaznable.core.miner.MinerConfig;

import java.util.List;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyEventBusConfig {

    @MinerConfig(dataId = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();

    @MinerConfig(dataId = "eventbus.interceptors")
    List<BunnyEventBusInterceptor> interceptors();
}
