package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.github.charlemaznable.core.vertx.spring.VertxImport;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;

@Configuration
@VertxImport
public class BunnyVertxConfiguration {

    @Bean("bunny.rabbit.VertxOptions")
    public VertxOptions vertxOptions(@Nullable BunnyVertxConfig bunnyVertxConfig) {
        return nullThen(bunnyVertxConfig, () -> getConfig(BunnyVertxConfig.class)).vertxOptions();
    }
}
