package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.plugin.ServePlugin;

import javax.annotation.Nonnull;

public interface ServePluginLoader {

    /**
     * 依据服务类型获取服务插件
     */
    @Nonnull
    ServePlugin load(String serveType);
}
