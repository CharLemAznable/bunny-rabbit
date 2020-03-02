package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public final class TestHandler
        implements BunnyHandler<TestRequest, TestResponse> {

    @Override
    public String address() {
        return "/test";
    }

    @Override
    public Class<TestRequest> getRequestClass() {
        return TestRequest.class;
    }

    @Override
    public void execute(TestRequest request,
                        Handler<AsyncResult<TestResponse>> handler) {
        handler.handle(Future.succeededFuture());
    }
}
