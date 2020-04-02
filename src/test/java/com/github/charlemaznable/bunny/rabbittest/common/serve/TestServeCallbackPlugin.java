package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.ERROR;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SERVE_CALLBACK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SUCCESS;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("TestServeCallback")
public class TestServeCallbackPlugin implements ServeCallbackPlugin {

    @Override
    public void checkRequest(Map<String, Object> context,
                             Map<String, Object> request,
                             Handler<AsyncResult<Integer>> handler) {
        assertNotNull(context.get(MtcpContext.TENANT_ID));
        assertNotNull(context.get(MtcpContext.TENANT_CODE));
        assertEquals(context.get(MtcpContext.TENANT_ID), context.get(MtcpContext.TENANT_CODE));

        if (SUCCESS.equals(request.get(SERVE_CALLBACK_KEY))) {
            handler.handle(succeededFuture(1));

        } else if (FAILURE.equals(request.get(SERVE_CALLBACK_KEY))) {
            handler.handle(succeededFuture(0));

        } else if (ERROR.equals(request.get(SERVE_CALLBACK_KEY))) {
            handler.handle(failedFuture(new BunnyException(
                    "TEST_SERVE_CALLBACK_FAILED", "Test Serve Callback Failed")));

        } else {
            throw new MockException("Serve Callback Error");
        }
    }
}
