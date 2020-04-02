package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;

import javax.annotation.Nonnull;

public interface ServeCallbackPluginLoader {

    /**
     * 依据服务名称获取服务回调插件
     */
    @Nonnull
    ServeCallbackPlugin load(String serveName);
}
