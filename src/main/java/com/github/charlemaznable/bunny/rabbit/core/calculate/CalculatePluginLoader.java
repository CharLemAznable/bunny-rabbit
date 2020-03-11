package com.github.charlemaznable.bunny.rabbit.core.calculate;

import com.github.charlemaznable.bunny.plugin.CalculatePlugin;

import javax.annotation.Nonnull;

public interface CalculatePluginLoader {

    /**
     * 依据计费类型获取计费插件
     */
    @Nonnull
    CalculatePlugin load(String chargingType);
}
