package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerInterceptor;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nullable;

public class TestHttpServerInterceptor implements BunnyHttpServerInterceptor {

    @Override
    public void preHandle(RoutingContext routingContext) {
        TestContext.setContext("httpserver");
    }

    @Override
    public void afterCompletion(RoutingContext routingContext,
                                @Nullable String response,
                                @Nullable Throwable throwable) {
        TestContext.clear();
    }
}
