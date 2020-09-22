package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.github.charlemaznable.core.miner.MinerConfig;

@MinerConfig
public interface IllegalVertxConfig extends BunnyVertxConfig {

    @MinerConfig(group = "VertxOptions", dataId = "bunnyIllegal",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    @Override
    String rawVertxOptions();
}
