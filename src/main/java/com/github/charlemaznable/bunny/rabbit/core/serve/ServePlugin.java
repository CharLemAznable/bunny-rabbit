package com.github.charlemaznable.bunny.rabbit.core.serve;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Map;

public interface ServePlugin {

    void serve(Map<String, Object> request,
               Handler<AsyncResult<Map<String, Object>>> handler);

    boolean checkResponse(Map<String, Object> response);
}
