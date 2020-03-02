package com.github.charlemaznable.bunny.rabbit.vertx.common;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public interface BunnyHandler<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse> {

    String address();

    Class<T> getRequestClass();

    void execute(T request, Handler<AsyncResult<U>> handler);

    default <V> void executeBlocking(Handler<Promise<V>> blockingCodeHandler,
                                     Handler<AsyncResult<V>> resultHandler) {
        Vertx.currentContext().executeBlocking(blockingCodeHandler, false, resultHandler);
    }
}
