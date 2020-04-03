package com.github.charlemaznable.bunny.rabbittest.common.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.n3r.eql.mtcp.MtcpContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.common.serve.ServeCommon.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component("ServeCalculate")
public class ServeCalculatePlugin implements CalculatePlugin {

    static final String CALC_KEY = "CALC";
    static final String CALCED_KEY = "CALCED";

    @Override
    public void calculate(Map<String, Object> context,
                          Map<String, Object> chargingParameters,
                          Handler<AsyncResult<Integer>> handler) {
        assertNotNull(context.get(MtcpContext.TENANT_ID));
        assertNotNull(context.get(MtcpContext.TENANT_CODE));
        assertEquals(context.get(MtcpContext.TENANT_ID), context.get(MtcpContext.TENANT_CODE));

        if (SUCCESS.equals(chargingParameters.get(CALC_KEY))) {
            context.put(CALCED_KEY, SUCCESS);
            handler.handle(Future.succeededFuture(1));

        } else if (FAILURE.equals(chargingParameters.get(CALC_KEY))) {
            handler.handle(Future.failedFuture(new BunnyException(
                    "SERVE_CALCULATE_FAILED", "Serve Calculate Failed")));

        } else {
            throw new MockException("Serve Calculate Error");
        }
    }
}
