package com.github.charlemaznable.bunny.rabbit.core.common;

import java.util.List;

public interface BunnyInterceptorLoader {

    List<BunnyInterceptor> loadInterceptors();
}
