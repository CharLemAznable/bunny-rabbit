package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface BunnyHandler<T extends BunnyBaseRequest<? extends U>, U extends BunnyBaseResponse> {

    String address();

    Class<? extends T> getRequestClass();

    void execute(T request, Handler<AsyncResult<U>> handler);
}
