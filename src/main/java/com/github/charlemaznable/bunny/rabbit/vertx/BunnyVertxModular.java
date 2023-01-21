package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.github.charlemaznable.configservice.ConfigModular;
import com.github.charlemaznable.core.guice.Modulee;
import com.github.charlemaznable.core.vertx.guice.VertxModular;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.util.Providers;
import io.vertx.core.VertxOptions;

import javax.annotation.Nullable;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;

public final class BunnyVertxModular {

    private final Module configModule;
    private final VertxModular vertxModular;

    public BunnyVertxModular() {
        this((BunnyVertxConfig) null);
    }

    public BunnyVertxModular(Class<? extends BunnyVertxConfig> configClass) {
        this(new ConfigModular().bindClasses(configClass).createModule());
    }

    public BunnyVertxModular(BunnyVertxConfig configImpl) {
        this(new AbstractModule() {
            @Override
            protected void configure() {
                bind(BunnyVertxConfig.class).toProvider(Providers.of(configImpl));
            }
        });
    }

    public BunnyVertxModular(Module configModule) {
        this.configModule = configModule;
        this.vertxModular = new VertxModular(new AbstractModule() {
            @Provides
            public VertxOptions vertxOptions(@Nullable BunnyVertxConfig bunnyVertxConfig) {
                return nullThen(bunnyVertxConfig, () -> getConfig(BunnyVertxConfig.class)).vertxOptions();
            }
        });
    }

    public Module createModule() {
        return Modulee.combine(configModule, vertxModular.createModule());
    }
}
