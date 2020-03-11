package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;

import java.util.List;

public interface BunnyInterceptorLoader {

    List<BunnyInterceptor> loadInterceptors();
}
