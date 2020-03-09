package com.github.charlemaznable.bunny.rabbit.guice.loader;

import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.val;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CALCULATE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public final class CalculatePluginLoaderImpl implements CalculatePluginLoader {

    private final Injector injector;
    private final PluginNameMapper pluginNameMapper;

    @Inject
    public CalculatePluginLoaderImpl(Injector injector,
                                     PluginNameMapper pluginNameMapper) {
        this.injector = injector;
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public CalculatePlugin load(String chargingType) {
        val pluginName = pluginNameMapper.calculatePluginName(chargingType);
        try {
            return checkNotNull(injector.getInstance(
                    get(CalculatePlugin.class, named(pluginName))));
        } catch (Exception e) {
            throw CALCULATE_FAILED.exception(pluginName + " Plugin Not Found");
        }
    }
}
