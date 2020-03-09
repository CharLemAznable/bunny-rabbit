package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.SERVE_CALLBACK_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

@Component
public final class ServeCallbackPluginLoaderImpl implements ServeCallbackPluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;

    @Inject
    public ServeCallbackPluginLoaderImpl(Injector injector,
                                         PluginNameMapper pluginNameMapper) {
        this.injector = injector;
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public ServeCallbackPlugin load(String serveType) {
        val pluginName = pluginNameMapper.serveCallbackPluginName(serveType);
        try {
            return checkNotNull(injector.getInstance(
                    get(ServeCallbackPlugin.class, named(pluginName))));
        } catch (Exception e) {
            throw SERVE_CALLBACK_FAILED.exception(pluginName + " Plugin Not Found");
        }
    }
}
