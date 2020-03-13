package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.plugin.ServeCallbackPlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SERVE_CALLBACK_KEY;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCallbackCommon.SUCCESS;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.isNull;
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
        val callback = request.get(SERVE_CALLBACK_KEY);
        if (isNull(callback)) {
            handler.handle(failedFuture(
                    new MockException("Serve Callback Error")));
        } else {
            handler.handle(succeededFuture(SUCCESS.equals(callback) ? 1 : 0));
        }
    }
}
