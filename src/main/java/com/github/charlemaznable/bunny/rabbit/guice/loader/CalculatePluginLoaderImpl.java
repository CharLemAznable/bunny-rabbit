package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.common.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CALCULATE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.miner.MinerFactory.getMiner;
import static com.google.common.cache.CacheLoader.from;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public final class CalculatePluginLoaderImpl implements CalculatePluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, CalculatePlugin> cache
            = LoadingCachee.simpleCache(from(this::loadCalculatePlugin));

    @Inject
    public CalculatePluginLoaderImpl(Injector injector,
                                     @Nullable PluginNameMapper pluginNameMapper) {
        this.injector = checkNotNull(injector);
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getMiner(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public CalculatePlugin load(String serveName) {
        return LoadingCachee.get(cache, serveName);
    }

    private CalculatePlugin loadCalculatePlugin(String serveName) {
        val pluginName = pluginNameMapper.calculatePluginName(serveName);
        try {
            return checkNotNull(injector.getInstance(
                    get(CalculatePlugin.class, named(pluginName))));
        } catch (Exception e) {
            throw CALCULATE_FAILED.exception(pluginName + " Plugin Not Found");
        }
    }
}
