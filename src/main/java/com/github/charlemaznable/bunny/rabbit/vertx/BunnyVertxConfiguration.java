package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;

@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringFacetCodeInspection"})
@Configuration
public class BunnyVertxConfiguration implements Provider<VertxOptions> {

    private final BunnyConfig bunnyConfig;

    @Inject
    @Autowired
    public BunnyVertxConfiguration(@Nullable BunnyConfig bunnyConfig) {
        this.bunnyConfig = nullThen(bunnyConfig,
                () -> getMiner(BunnyConfig.class));
    }

    @Bean
    @Override
    public VertxOptions get() {
        return new VertxOptions().setWorkerPoolSize(bunnyConfig.workerPoolSize())
                .setMaxWorkerExecuteTime(bunnyConfig.maxWorkerExecuteTime());
    }
}
