package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.rabbit.core.calcute.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.calcute.CalculatePluginLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CALCULATE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

@Component
public final class CalculatePluginLoaderImpl implements CalculatePluginLoader {

    private final PluginNameMapper pluginNameMapper;

    @Autowired
    public CalculatePluginLoaderImpl(PluginNameMapper pluginNameMapper) {
        this.pluginNameMapper = pluginNameMapper;
    }

    @Nonnull
    @Override
    public CalculatePlugin load(String chargingType) {
        val pluginName = pluginNameMapper.calculatePluginName(chargingType);
        return checkNotNull(SpringContext.getBean(pluginName, CalculatePlugin.class),
                CALCULATE_FAILED.exception(pluginName + " Plugin Not Found"));
    }
}
