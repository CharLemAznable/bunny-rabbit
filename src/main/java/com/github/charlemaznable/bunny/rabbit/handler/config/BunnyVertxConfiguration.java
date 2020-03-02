package com.github.charlemaznable.bunny.rabbit.handler.config;

import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyVertxConfig;
import com.google.inject.Inject;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;

@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringFacetCodeInspection"})
@Configuration
public class BunnyVertxConfiguration {

    private final BunnyVertxConfig config;

    public BunnyVertxConfiguration() {
        this(null);
    }

    @Inject
    @Autowired
    public BunnyVertxConfiguration(@Nullable BunnyVertxConfig config) {
        this.config = nullThen(config, () ->
                getMiner(BunnyVertxConfig.class));
    }

    @Bean
    public VertxOptions vertxOptions() {
        return new VertxOptions()
                .setWorkerPoolSize(config.workerPoolSize())
                .setMaxWorkerExecuteTime(config.maxWorkerExecuteTime());
    }
}
