package com.github.charlemaznable.bunny.rabbit.vertx;

import io.vertx.ext.web.RoutingContext;
import org.n3r.diamond.client.cache.ParamsAppliable;

import javax.annotation.Nullable;

public interface BunnyHttpServerInterceptor extends ParamsAppliable {

    default void preHandle(RoutingContext routingContext) {}

    default void afterCompletion(RoutingContext routingContext,
                                 @Nullable String response,
                                 @Nullable Throwable throwable) {}

    @Override
    default void applyParams(String[] params) {}
}
