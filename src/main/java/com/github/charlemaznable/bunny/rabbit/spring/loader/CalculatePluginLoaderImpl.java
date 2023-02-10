package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.common.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CALCULATE_FAILED;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.google.common.cache.CacheLoader.from;

public final class CalculatePluginLoaderImpl implements CalculatePluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private final LoadingCache<String, CalculatePlugin> cache
            = LoadingCachee.simpleCache(from(this::loadCalculatePlugin));

    public CalculatePluginLoaderImpl(@Nullable PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getConfig(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public CalculatePlugin load(String serveName) {
        return LoadingCachee.get(cache, serveName);
    }

    private CalculatePlugin loadCalculatePlugin(String serveName) {
        val pluginName = pluginNameMapper.calculatePluginName(serveName);
        return checkNotNull(SpringContext.getBean(pluginName, CalculatePlugin.class),
                CALCULATE_FAILED.exception(pluginName + " Plugin Not Found"));
    }
}
