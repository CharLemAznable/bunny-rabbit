package com.github.charlemaznable.bunny.rabbittest.common.calculate;

import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbittest.common.common.MockException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.charlemaznable.core.lang.Condition.nonNull;
import static com.github.charlemaznable.core.lang.Mapp.getInt;

@Component("TestCalculate")
public class TestCalculatePlugin implements CalculatePlugin {

    static final String RESULT_KEY = "RESULT";
    static final int RESULT_1 = 1;
    static final int RESULT_2 = 2;
    static final String CALCULATE_KEY = "CALC";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";

    @Override
    public void calculate(Map<String, Object> context,
                          Map<String, Object> chargingParameters,
                          Handler<AsyncResult<Integer>> handler) {
        if (SUCCESS.equals(chargingParameters.get(CALCULATE_KEY))) {
            val result = nonNull(getInt(context, RESULT_KEY),
                    getInt(chargingParameters, RESULT_KEY));
            handler.handle(Future.succeededFuture(result));

        } else if (FAILURE.equals(chargingParameters.get(CALCULATE_KEY))) {
            handler.handle(Future.failedFuture(new BunnyException(
                    "TEST_CALCULATE_FAILED", "Test Calculate Failed")));

        } else {
            throw new MockException("Calculate Error");
        }
    }
}
