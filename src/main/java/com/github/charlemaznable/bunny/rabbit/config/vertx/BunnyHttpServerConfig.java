package com.github.charlemaznable.bunny.rabbit.config.vertx;

import com.github.charlemaznable.core.miner.MinerConfig;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyHttpServerConfig {

    @MinerConfig(dataId = "deploy.context-path", defaultValue = "/bunny")
    String contextPath();

    @MinerConfig(dataId = "deploy.port", defaultValue = "22114")
    int port();
}
