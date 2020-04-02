package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.plugin.SwitchPlugin;

import javax.annotation.Nonnull;

public interface SwitchPluginLoader {

    /**
     * 依据服务名称获取开关插件
     */
    @Nonnull
    SwitchPlugin load(String serveName);
}
