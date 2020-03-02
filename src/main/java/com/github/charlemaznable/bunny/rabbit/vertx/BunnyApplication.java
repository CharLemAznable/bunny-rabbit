package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyVerticleDeployment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Slf4j
public final class BunnyApplication {

    @Getter
    @Accessors(fluent = true)
    private final Iterable<BunnyHandler<?, ?>> handlers;

    public BunnyApplication(BunnyHandler<?, ?>... handlers) {
        this(newArrayList(handlers));
    }

    public BunnyApplication(Iterator<BunnyHandler<?, ?>> handlers) {
        this(newArrayList(handlers));
    }

    public BunnyApplication(Iterable<BunnyHandler<?, ?>> handlers) {
        this.handlers = newArrayList(handlers);
    }

    public <T> BunnyApplication(T[] handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(newArrayList(handlerParameters), handlerBuilder);
    }

    public <T> BunnyApplication(Iterator<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(newArrayList(handlerParameters), handlerBuilder);
    }

    public <T> BunnyApplication(Iterable<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        checkNotNull(handlerBuilder);
        this.handlers = newArrayList(handlerParameters).stream()
                .map(handlerBuilder).collect(Collectors.toList());
    }

    public void deploy(Vertx vertx) {
        deploy(vertx, null);
    }

    public void deploy(Vertx vertx, Handler<AsyncResult<BunnyVerticleDeployment>> completer) {
        vertx.deployVerticle(new BunnyEventBusVerticle(this.handlers),
                wrapHandler(EVENT_BUS_VERTICLE, completer));
        vertx.deployVerticle(new BunnyHttpServerVerticle(this.handlers),
                wrapHandler(HTTP_SERVER_VERTICLE, completer));
    }

    private Handler<AsyncResult<String>> wrapHandler(
            String verticleName, Handler<AsyncResult<BunnyVerticleDeployment>> completer) {
        return arDeployment -> {
            if (null == completer) return;

            if (arDeployment.failed()) {
                log.warn("Verticle:{} deploy failed:",
                        verticleName, arDeployment.cause());
            }
            val deployment = new BunnyVerticleDeployment();
            deployment.setVerticleName(verticleName);
            deployment.setDeploymentId(arDeployment.result());
            completer.handle(Future.succeededFuture(deployment));
        };
    }
}
