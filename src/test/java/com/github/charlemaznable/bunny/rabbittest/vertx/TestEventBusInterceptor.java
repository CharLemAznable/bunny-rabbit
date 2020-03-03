package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusInterceptor;
import io.vertx.core.eventbus.Message;

import javax.annotation.Nullable;

public class TestEventBusInterceptor implements BunnyEventBusInterceptor {

    @Override
    public void preHandle(Message<String> message) {
        TestContext.setContext("eventbus");
    }

    @Override
    public void afterCompletion(Message<String> message,
                                @Nullable String response,
                                @Nullable Throwable throwable) {
        TestContext.clear();
    }
}
