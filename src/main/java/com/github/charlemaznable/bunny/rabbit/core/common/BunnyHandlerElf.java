package com.github.charlemaznable.bunny.rabbit.core.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public final class BunnyHandlerElf {

    private BunnyHandlerElf() {
        throw new UnsupportedOperationException();
    }

    public static <V> void executeBlocking(
            Handler<Promise<V>> blockingCodeHandler,
            Handler<AsyncResult<V>> resultHandler) {
        Vertx.currentContext().executeBlocking(block -> {
            try {
                blockingCodeHandler.handle(block);
            } catch (Exception e) {
                block.fail(e);
            }
        }, false, resultHandler);
    }
}
