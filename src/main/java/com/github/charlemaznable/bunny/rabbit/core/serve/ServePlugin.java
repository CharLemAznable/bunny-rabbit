package com.github.charlemaznable.bunny.rabbit.core.serve;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.github.charlemaznable.core.lang.Mapp.newHashMap;

public interface ServePlugin {

    @Nonnull
    default Map<String, Object> composeRequest(
            Map<String, String> chargingParameters) {
        return newHashMap(chargingParameters);
    }

    void serve(Map<String, Object> request,
               Handler<AsyncResult<Map<String, Object>>> handler);

    boolean checkResponse(Map<String, Object> response);
}
