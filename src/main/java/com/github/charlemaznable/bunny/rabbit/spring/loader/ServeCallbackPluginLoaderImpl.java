package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_CALLBACK_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.google.common.cache.CacheLoader.from;

@Component
public final class ServeCallbackPluginLoaderImpl implements ServeCallbackPluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServeCallbackPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeCallbackPlugin));

    @Autowired
    public ServeCallbackPluginLoaderImpl(PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public ServeCallbackPlugin load(String serveType) {
        return LoadingCachee.get(cache, serveType);
    }

    private ServeCallbackPlugin loadServeCallbackPlugin(String serveType) {
        val pluginName = pluginNameMapper.serveCallbackPluginName(serveType);
        return checkNotNull(SpringContext.getBean(pluginName, ServeCallbackPlugin.class),
                SERVE_CALLBACK_FAILED.exception(pluginName + " Plugin Not Found"));
    }
}
