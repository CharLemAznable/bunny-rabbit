package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
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
        val response = request.createResponse();
        response.succeed();
        response.setTestResult(request.getTestParameter() + ":" + TestContext.getContext());
        handler.handle(Future.succeededFuture(response));
    }
}
