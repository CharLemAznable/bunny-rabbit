package com.github.charlemaznable.bunny.rabbit.config.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerInterceptor;
import com.github.charlemaznable.core.miner.MinerConfig;

import java.util.List;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyHttpServerConfig {

    @MinerConfig(dataId = "httpserver.context-path", defaultValue = "/bunny")
    String contextPath();

    @MinerConfig(dataId = "httpserver.port", defaultValue = "22114")
    int port();

    @MinerConfig(dataId = "httpserver.interceptors")
    List<BunnyHttpServerInterceptor> interceptors();
}
