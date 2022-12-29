package com.github.charlemaznable.bunny.rabbit.spring.loader;

import com.github.charlemaznable.bunny.plugin.SwitchPlugin;
import com.github.charlemaznable.bunny.rabbit.core.common.SwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.mapper.PluginNameMapper;
import com.github.charlemaznable.core.lang.LoadingCachee;
import com.github.charlemaznable.core.spring.SpringContext;
import com.google.common.cache.LoadingCache;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.google.common.cache.CacheLoader.from;

@Component
public final class SwitchPluginLoaderImpl implements SwitchPluginLoader {

    private final PluginNameMapper pluginNameMapper;
    private final LoadingCache<String, SwitchPlugin> cache
            = LoadingCachee.simpleCache(from(this::loadServeSwitchPlugin));

    @Autowired
    public SwitchPluginLoaderImpl(@Nullable PluginNameMapper pluginNameMapper) {
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
        return nullThen(SpringContext.getBean(pluginName,
                SwitchPlugin.class), () -> new SwitchPlugin() {});
    }
}
