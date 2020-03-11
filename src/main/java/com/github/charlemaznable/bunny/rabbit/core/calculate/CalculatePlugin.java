package com.github.charlemaznable.bunny.rabbit.core.calculate;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Map;

public interface CalculatePlugin {

    void calculate(Map<String, Object> chargingParameters,
                   Handler<AsyncResult<CalculateResult>> handler);
}
