package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServePlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.google.common.cache.CacheLoader.from;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public final class ServePluginLoaderImpl implements ServePluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServePlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServePlugin));

    @Inject
    public ServePluginLoaderImpl(Injector injector,
                                 PluginNameMapper pluginNameMapper) {
        this.injector = injector;
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public ServePlugin load(String serveType) {
        return LoadingCachee.get(cache, serveType);
    }

    private ServePlugin loadServePlugin(String serveType) {
        val pluginName = pluginNameMapper.servePluginName(serveType);
        try {
            return checkNotNull(injector.getInstance(
                    get(ServePlugin.class, named(pluginName))));
        } catch (Exception e) {
            throw SERVE_FAILED.exception(pluginName + " Plugin Not Found");
        }
    }
}
