package com.github.charlemaznable.bunny.rabbittest.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.rabbit.core.calcute.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.core.calcute.CalculateResult;
import com.github.charlemaznable.bunny.rabbittest.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.CALCULATE_KEY;
import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.FAILURE;
import static com.github.charlemaznable.bunny.rabbittest.serve.ServeTest.SUCCESS;

@Component("ServeCalculate")
public class ServeCalculatePlugin implements CalculatePlugin {

    @Override
    public void calculate(Map<String, String> chargingParameters,
                          Handler<AsyncResult<CalculateResult>> handler) {
        if (SUCCESS.equals(chargingParameters.get(CALCULATE_KEY))) {
            val calculateResult = new CalculateResult();
            calculateResult.setCalculate(1);
            calculateResult.setUnit("条");
            handler.handle(Future.succeededFuture(calculateResult));
        } else if (FAILURE.equals(chargingParameters.get(CALCULATE_KEY))) {
            handler.handle(Future.failedFuture(new BunnyException(
                    "SERVE_CALCULATE_FAILED", "Serve Calculate Failed")));
        } else {
            throw new MockException("Serve Calculate Error");
        }
    }
}
