package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_CALLBACK_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static com.google.common.cache.CacheLoader.from;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

@Component
public final class ServeCallbackPluginLoaderImpl implements ServeCallbackPluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;
    private LoadingCache<String, ServeCallbackPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeCallbackPlugin));

    @Inject
    public ServeCallbackPluginLoaderImpl(Injector injector,
                                         @Nullable PluginNameMapper pluginNameMapper) {
        this.injector = checkNotNull(injector);
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getMiner(PluginNameMapper.class));
    }

    @Nonnull
    @Override
    public ServeCallbackPlugin load(String serveType) {
        return LoadingCachee.get(cache, serveType);
    }

    private ServeCallbackPlugin loadServeCallbackPlugin(String serveType) {
        val pluginName = pluginNameMapper.serveCallbackPluginName(serveType);
        try {
            return checkNotNull(injector.getInstance(
                    get(ServeCallbackPlugin.class, named(pluginName))));
        } catch (Exception e) {
            throw SERVE_CALLBACK_FAILED.exception(pluginName + " Plugin Not Found");
        }
    }
}
