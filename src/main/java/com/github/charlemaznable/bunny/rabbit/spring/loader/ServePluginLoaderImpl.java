package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServePlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.google.common.cache.CacheLoader.from;

@Component
public final class ServePluginLoaderImpl implements ServePluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServePlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServePlugin));

    @Autowired
    public ServePluginLoaderImpl(PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public ServePlugin load(String serveType) {
        return LoadingCachee.get(cache, serveType);
    }

    private ServePlugin loadServePlugin(String serveType) {
        val pluginName = pluginNameMapper.servePluginName(serveType);
        return checkNotNull(SpringContext.getBean(pluginName, ServePlugin.class),
                SERVE_FAILED.exception(pluginName + " Plugin Not Found"));
    }
}
