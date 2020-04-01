package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.ServeSwitchPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeSwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static com.google.common.cache.CacheLoader.from;

@Component
public final class ServeSwitchPluginLoaderImpl implements ServeSwitchPluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServeSwitchPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeSwitchPlugin));

    @Autowired
    public ServeSwitchPluginLoaderImpl(@Nullable PluginNameMapper pluginNameMapper) {
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
        return nullThen(SpringContext.getBean(pluginName,
                ServeSwitchPlugin.class), () -> new ServeSwitchPlugin() {});
    }
}
