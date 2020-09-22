package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.BunnyVertxConfig;
import com.github.charlemaznable.core.guice.Modulee;
import com.github.charlemaznable.core.miner.MinerModular;
import com.github.charlemaznable.core.vertx.guice.VertxModular;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Providers;

public final class BunnyVertxModular {

    private final Module configModule;
    private VertxModular vertxModular;

    public BunnyVertxModular() {
        this((BunnyVertxConfig) null);
    }

    public BunnyVertxModular(Class<? extends BunnyVertxConfig> configClass) {
        this(new MinerModular().bindClasses(configClass).createModule());
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
        this.vertxModular = new VertxModular(BunnyVertxConfiguration.class);
    }

    public Module createModule() {
        return Modulee.combine(configModule, vertxModular.createModule());
    }
}
