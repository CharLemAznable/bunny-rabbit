package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.core.miner.MinerConfig;
import io.vertx.core.VertxOptions;

import static com.github.charlemaznable.vertx.diamond.VertxDiamondElf.parseStoneToVertxOptions;

@MinerConfig
public interface BunnyVertxConfig {

    @MinerConfig(group = "VertxOptions", dataId = "bunny",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    String rawVertxOptions();

    default VertxOptions vertxOptions() {
        return parseStoneToVertxOptions(rawVertxOptions());
    }
}
