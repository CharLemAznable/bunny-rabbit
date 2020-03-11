package com.github.charlemaznable.bunny.rabbittest.common.calculate;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.plugin.CalculateResult;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TestCalculate")
public class TestCalculatePlugin implements CalculatePlugin {

    static final String CALCULATE_KEY = "CALC";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";

    @Override
    public void calculate(Map<String, Object> chargingParameters,
                          Handler<AsyncResult<CalculateResult>> handler) {
        if (SUCCESS.equals(chargingParameters.get(CALCULATE_KEY))) {
            val calculateResult = new CalculateResult();
            calculateResult.setCalculate(1);
            calculateResult.setUnit("条");
            handler.handle(Future.succeededFuture(calculateResult));
        } else if (FAILURE.equals(chargingParameters.get(CALCULATE_KEY))) {
            handler.handle(Future.failedFuture(new BunnyException(
                    "TEST_CALCULATE_FAILED", "Test Calculate Failed")));
        } else {
            throw new MockException("Calculate Error");
        }
    }
}
