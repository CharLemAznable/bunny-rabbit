package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import org.n3r.diamond.client.cache.ParamsAppliable;

import javax.annotation.Nullable;

public interface BunnyInterceptor extends ParamsAppliable {

    default void preHandle(BunnyBaseRequest<?> request) {}

    default void afterCompletion(@Nullable BunnyBaseRequest<?> request,
                                 @Nullable BunnyBaseResponse response,
                                 @Nullable Throwable throwable) {}

    @Override
    default void applyParams(String[] params) {}
}
