package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.google.inject.Inject;

import java.util.List;
import java.util.Set;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

public final class BunnyHandlerLoaderImpl implements BunnyHandlerLoader {

    private final List<BunnyHandler> handlers;

    @Inject
    public BunnyHandlerLoaderImpl(Set<BunnyHandler> handlers) {
        this.handlers = newArrayList(handlers);
    }

    @Override
    public List<BunnyHandler> loadHandlers() {
        return handlers;
    }
}
