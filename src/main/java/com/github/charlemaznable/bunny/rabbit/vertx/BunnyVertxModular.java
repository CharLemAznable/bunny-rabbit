package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.core.vertx.guice.VertxModular;
import com.google.inject.Module;

public final class BunnyVertxModular {

    private VertxModular vertxModular;

    public BunnyVertxModular() {
        this.vertxModular = new VertxModular(BunnyVertxConfiguration.class);
    }

    public Module createModule() {
        return vertxModular.createModule();
    }
}
