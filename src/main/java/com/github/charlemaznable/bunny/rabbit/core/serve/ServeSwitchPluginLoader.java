package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.plugin.ServeSwitchPlugin;

import javax.annotation.Nonnull;

public interface ServeSwitchPluginLoader {

    /**
     * 依据服务类型获取服务开关插件
     */
    @Nonnull
    ServeSwitchPlugin load(String serveType);
}
