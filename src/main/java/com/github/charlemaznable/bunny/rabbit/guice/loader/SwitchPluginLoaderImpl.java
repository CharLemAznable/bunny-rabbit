package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.plugin.SwitchPlugin;
import com.github.charlemaznable.bunny.rabbit.core.common.SwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.google.common.cache.CacheLoader.from;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public final class SwitchPluginLoaderImpl implements SwitchPluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, SwitchPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeSwitchPlugin));

    @Inject
    public SwitchPluginLoaderImpl(Injector injector,
                                  @Nullable PluginNameMapper pluginNameMapper) {
        this.injector = checkNotNull(injector);
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getConfig(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public SwitchPlugin load(String serveName) {
        return LoadingCachee.get(cache, serveName);
    }

    private SwitchPlugin loadServeSwitchPlugin(String serveName) {
        val pluginName = pluginNameMapper.serveSwitchPluginName(serveName);
        try {
            return checkNotNull(injector.getInstance(
                    get(SwitchPlugin.class, named(pluginName))));
        } catch (Exception e) {
            return new SwitchPlugin() {};
        }
    }
}
