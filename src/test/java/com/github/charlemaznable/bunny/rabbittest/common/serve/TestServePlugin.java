package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.ServePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SERVE_CHECK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SERVE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SUCCESS;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.UNDEFINED;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("TestServe")
public class TestServePlugin implements ServePlugin {

    @Override
    public void serve(Map<String, Object> context,
                      Map<String, Object> request,
                      Handler<AsyncResult<Map<String, Object>>> handler) {
        assertNotNull(context.get(MtcpContext.TENANT_ID));
        assertNotNull(context.get(MtcpContext.TENANT_CODE));
        assertEquals(context.get(MtcpContext.TENANT_ID), context.get(MtcpContext.TENANT_CODE));
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
    public void checkResponse(Map<String, Object> context,
                              Map<String, Object> response,
                              Handler<AsyncResult<Integer>> handler) {
        assertNotNull(context.get(MtcpContext.TENANT_ID));
        assertNotNull(context.get(MtcpContext.TENANT_CODE));
        assertEquals(context.get(MtcpContext.TENANT_ID), context.get(MtcpContext.TENANT_CODE));
        if (SUCCESS.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture(1));
        } else if (FAILURE.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture(0));
        } else if (UNDEFINED.equals(response.get(SERVE_CHECK_KEY))) {
            handler.handle(succeededFuture());
        } else {
            handler.handle(failedFuture(new MockException("Serve Check Error")));
        }
    }
}
