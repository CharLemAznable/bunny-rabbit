package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.configservice.Config;
import com.github.charlemaznable.configservice.impl.ParseAsVertxOptions;
import io.vertx.core.VertxOptions;

@Config
public interface BunnyVertxConfig {

    @Config(keyset = "VertxOptions", key = "${bunny-vertx-config:-bunny}",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    @ParseAsVertxOptions
    VertxOptions vertxOptions();
}
