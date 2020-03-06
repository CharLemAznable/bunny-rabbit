package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptor;
import com.github.charlemaznable.core.miner.MinerConfig;

import java.util.List;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyConfig {

    @MinerConfig(dataId = "vertx.worker-pool-size", defaultValue = "64")
    int workerPoolSize();

    @MinerConfig(dataId = "vertx.max-worker-execute-time", defaultValue = "60000000000")
    long maxWorkerExecuteTime(); // in NANOSECONDS

    @MinerConfig(dataId = "eventbus.address-prefix", defaultValue = "/bunny")
    String addressPrefix();

    @MinerConfig(dataId = "httpserver.context-path", defaultValue = "/bunny")
    String contextPath();

    @MinerConfig(dataId = "httpserver.port", defaultValue = "22114")
    int port();

    @MinerConfig(dataId = "interceptors")
    List<BunnyInterceptor> interceptors();
}
