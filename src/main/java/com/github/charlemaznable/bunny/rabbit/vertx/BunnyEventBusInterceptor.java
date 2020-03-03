package com.github.charlemaznable.bunny.rabbit.vertx;

import io.vertx.core.eventbus.Message;
import org.n3r.diamond.client.cache.ParamsAppliable;

import javax.annotation.Nullable;

public interface BunnyEventBusInterceptor extends ParamsAppliable {

    default void preHandle(Message<String> message) {}

    default void afterCompletion(Message<String> message,
                                 @Nullable String response,
                                 @Nullable Throwable throwable) {}

    @Override
    default void applyParams(String[] params) {}
}
