package com.github.charlemaznable.bunny.rabbit.core.calcute;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Map;

public interface CalculatePlugin {

    void calculate(Map<String, String> chargingParameters,
                   Handler<AsyncResult<CalculateResult>> handler);
}
