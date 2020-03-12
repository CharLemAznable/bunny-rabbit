package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.core.miner.MinerConfig;

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

    @MinerConfig(dataId = "callback.limit", defaultValue = "3")
    int callbackLimit(); // callback times limit

    @MinerConfig(dataId = "callback.delay", defaultValue = "60000")
    long callbackDelay(); // in MILLISECONDS
}
