package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.rabbit.handler.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.handler.plugin.CalculatePluginLoader;
import com.github.charlemaznable.core.spring.SpringContext;
import org.springframework.stereotype.Component;

@Component
public class CalculatePluginLoaderImpl implements CalculatePluginLoader {

    @Override
    public CalculatePlugin load(String pluginName) {
        return SpringContext.getBean(pluginName, CalculatePlugin.class);
    }
}
