package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.ServePlugin;
import com.github.charlemaznable.bunny.rabbit.core.common.ServePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_FAILED;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.google.common.cache.CacheLoader.from;

public final class ServePluginLoaderImpl implements ServePluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private final LoadingCache<String, ServePlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServePlugin));

    public ServePluginLoaderImpl(@Nullable PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getConfig(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public ServePlugin load(String serveName) {
        return LoadingCachee.get(cache, serveName);
    }

    private ServePlugin loadServePlugin(String serveName) {
        val pluginName = pluginNameMapper.servePluginName(serveName);
        return checkNotNull(SpringContext.getBean(pluginName, ServePlugin.class),
                SERVE_FAILED.exception(pluginName + " Plugin Not Found"));
    }
}
