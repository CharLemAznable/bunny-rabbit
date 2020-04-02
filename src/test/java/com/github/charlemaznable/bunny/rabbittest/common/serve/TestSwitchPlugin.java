package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.SwitchPlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SUCCESS;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("TestSwitch")
public class TestSwitchPlugin implements SwitchPlugin {

    static final String SWITCH_KEY = "SWITCH";
    static final String SWITCH_CONFIRM_KEY = "SWITCH_CONFIRM";

    @Override
    public void serveDeduct(Map<String, Object> context,
                            Map<String, Object> request,
                            Handler<AsyncResult<Boolean>> handler) {
        assertNotNull(context.get(MtcpContext.TENANT_ID));
        assertNotNull(context.get(MtcpContext.TENANT_CODE));
        assertEquals(context.get(MtcpContext.TENANT_ID), context.get(MtcpContext.TENANT_CODE));

        if (SUCCESS.equals(request.get(SWITCH_KEY))) {
            request.put(SWITCH_KEY, request.get(SWITCH_CONFIRM_KEY));
            handler.handle(succeededFuture(false));

        } else if (FAILURE.equals(request.get(SWITCH_KEY))) {
            handler.handle(failedFuture(new BunnyException(
                    "TEST_SERVE_SWITCH_FAILED", "Test Serve Switch Failed")));

        } else {
            throw new MockException("Serve Switch Error");
        }
    }
}
