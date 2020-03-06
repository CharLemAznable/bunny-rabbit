package com.github.charlemaznable.bunny.rabbittest.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.rabbit.core.serve.ServePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.SERVE_CHECK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.SERVE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.SUCCESS;

@Component("TestServe")
public class TestServePlugin implements ServePlugin {

    @Override
    public void serve(Map<String, Object> request,
                      Handler<AsyncResult<Map<String, Object>>> handler) {
        if (SUCCESS.equals(request.get(SERVE_KEY))) {
            handler.handle(Future.succeededFuture(request));
        } else if (FAILURE.equals(request.get(SERVE_KEY))) {
            handler.handle(Future.failedFuture(new BunnyException(
                    "TEST_SERVE_FAILED", "Test Serve Failed")));
        } else {
            throw new MockException("Serve Error");
        }
    }

    @Override
    public boolean checkResponse(Map<String, Object> response) {
        return SUCCESS.equals(response.get(SERVE_CHECK_KEY));
    }
}
