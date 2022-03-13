package com.github.charlemaznable.bunny.rabbit.config;

import com.github.charlemaznable.configservice.annotation.ConfigValueParse;
import com.github.charlemaznable.configservice.apollo.ApolloConfig;
import com.github.charlemaznable.configservice.diamond.DiamondConfig;
import com.github.charlemaznable.configservice.impl.VertxOptionsParser;
import io.vertx.core.VertxOptions;

@ApolloConfig
@DiamondConfig
public interface BunnyVertxConfig {

    @ApolloConfig(namespace = "VertxOptions", propertyName = "${bunny-vertx-config:-bunny}",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    @DiamondConfig(group = "VertxOptions", dataId = "${bunny-vertx-config:-bunny}",
            defaultValue = "workerPoolSize=64\nmaxWorkerExecuteTime=60000000000\n")
    @ConfigValueParse(VertxOptionsParser.class)
    VertxOptions vertxOptions();
}
