package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyInterceptorLoader;
import com.google.inject.Inject;

import java.util.List;
import java.util.Set;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;

public final class BunnyInterceptorLoaderImpl implements BunnyInterceptorLoader {

    private final List<BunnyInterceptor> interceptors;

    @Inject
    public BunnyInterceptorLoaderImpl(Set<BunnyInterceptor> interceptors) {
        this.interceptors = newArrayList(interceptors);
    }

    @Override
    public List<BunnyInterceptor> loadInterceptors() {
        return interceptors;
    }
}
