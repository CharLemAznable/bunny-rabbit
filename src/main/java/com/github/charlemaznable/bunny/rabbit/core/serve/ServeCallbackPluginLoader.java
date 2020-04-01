package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;

import javax.annotation.Nonnull;

public interface ServeCallbackPluginLoader {

    /**
     * 依据服务类型获取服务回调插件
     */
    @Nonnull
    ServeCallbackPlugin load(String serveType);
}
