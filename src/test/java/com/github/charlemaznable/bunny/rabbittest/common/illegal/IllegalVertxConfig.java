package com.github.charlemaznable.bunny.rabbittest.common.illegal;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.github.charlemaznable.configservice.impl.ParseAsVertxOptions;
import io.vertx.core.VertxOptions;

@DiamondConfig
public interface IllegalVertxConfig extends BunnyVertxConfig {

    @DiamondConfig(group = "VertxOptions", dataId = "bunnyIllegal",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    @ParseAsVertxOptions
    @Override
    VertxOptions vertxOptions();
}
