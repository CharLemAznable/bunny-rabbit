package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;

@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringFacetCodeInspection"})
@Configuration
public class BunnyVertxConfiguration implements Provider<VertxOptions> {

    private final BunnyVertxConfig bunnyVertxConfig;

    @Inject
    @Autowired
    public BunnyVertxConfiguration(@Nullable BunnyVertxConfig bunnyVertxConfig) {
        this.bunnyVertxConfig = nullThen(bunnyVertxConfig,
                () -> getConfig(BunnyVertxConfig.class));
    }

    @Bean
    @Override
    public VertxOptions get() {
        return bunnyVertxConfig.vertxOptions();
    }
}
