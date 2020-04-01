package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.plugin.ServeSwitchPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeSwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static com.google.common.cache.CacheLoader.from;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public final class ServeSwitchPluginLoaderImpl implements ServeSwitchPluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServeSwitchPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeSwitchPlugin));

    @Inject
    public ServeSwitchPluginLoaderImpl(Injector injector,
                                       @Nullable PluginNameMapper pluginNameMapper) {
        this.injector = checkNotNull(injector);
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getMiner(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public ServeSwitchPlugin load(String serveType) {
        return LoadingCachee.get(cache, serveType);
    }

    private ServeSwitchPlugin loadServeSwitchPlugin(String serveType) {
        val pluginName = pluginNameMapper.serveSwitchPluginName(serveType);
        try {
            return checkNotNull(injector.getInstance(
                    get(ServeSwitchPlugin.class, named(pluginName))));
        } catch (Exception e) {
            return new ServeSwitchPlugin() {};
        }
    }
}
