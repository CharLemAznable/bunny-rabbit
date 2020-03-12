package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.ServePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SERVE_CHECK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SERVE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SUCCESS;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.UNDEFINED;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

@Component("TestServe")
public class TestServePlugin implements ServePlugin {

    @Override
    public void serve(Map<String, Object> request,
                      Handler<AsyncResult<Map<String, Object>>> handler) {
        if (SUCCESS.equals(request.get(SERVE_KEY))) {
            handler.handle(succeededFuture(request));
        } else if (FAILURE.equals(request.get(SERVE_KEY))) {
            handler.handle(failedFuture(new BunnyException(
                    "TEST_SERVE_FAILED", "Test Serve Failed")));
        } else {
            throw new MockException("Serve Error");
        }
    }

    @Override
    public void checkResponse(Map<String, Object> response,
                              Handler<AsyncResult<Boolean>> handler) {
        if (SUCCESS.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture(true));
        } else if (FAILURE.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture(false));
        } else if (UNDEFINED.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture());
        } else {
            handler.handle(failedFuture(new MockException("Serve Check Error")));
        }
    }
}
