package com.github.charlemaznable.bunny.rabbittest.spring.calculate;

import com.github.charlemaznable.bunny.rabbit.handler.plugin.CalculatePlugin;
import com.github.charlemaznable.bunny.rabbit.handler.plugin.CalculateResult;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyException;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.await;

@Component("TestCalculate")
public class TestCalculatePlugin implements CalculatePlugin {

    static final String CALCULATE_KEY = "CALC";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";

    @Nonnull
    @Override
    public CalculateResult calculate(Map<String, String> chargingParameters) {
        await().pollDelay(Duration.ofMillis(5000)).until(() -> true); // 5 seconds

        if (SUCCESS.equals(chargingParameters.get(CALCULATE_KEY))) {
            return CalculateResult.successResult(1, "条");
        } else if (FAILURE.equals(chargingParameters.get(CALCULATE_KEY))) {
            return CalculateResult.failureResult("TEST_CALCULATE_FAILED", "Test Calculate Failed");
        } else {
            throw new BunnyException("TEST_CALCULATE_ERROR", "Test Calculate Error");
        }
    }
}
