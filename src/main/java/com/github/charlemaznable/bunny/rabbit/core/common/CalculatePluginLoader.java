package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.plugin.CalculatePlugin;

import javax.annotation.Nonnull;

public interface CalculatePluginLoader {

    /**
     * 依据服务名称获取计费插件
     */
    @Nonnull
    CalculatePlugin load(String serveName);
}
