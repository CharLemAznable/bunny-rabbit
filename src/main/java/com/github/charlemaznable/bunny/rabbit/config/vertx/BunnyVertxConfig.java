package com.github.charlemaznable.bunny.rabbit.config.vertx;

import com.github.charlemaznable.core.miner.MinerConfig;

@MinerConfig(group = "Bunny", dataId = "default")
public interface BunnyVertxConfig {

    @MinerConfig(defaultValue = "64")
    int workerPoolSize();

    @MinerConfig(defaultValue = "60000000000")
    long maxWorkerExecuteTime(); // in NANOSECONDS
}
