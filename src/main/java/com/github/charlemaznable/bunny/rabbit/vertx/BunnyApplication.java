package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyEventBusConfig;
import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyHttpServerConfig;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyVerticleDeployment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyEventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.vertx.BunnyHttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;

@Slf4j
public final class BunnyApplication {

    private final BunnyEventBusConfig eventBusConfig;
    private final BunnyHttpServerConfig httpServerConfig;
    private final Iterable<BunnyHandler<?, ?>> handlers;

    public BunnyApplication(BunnyHandler<?, ?>... handlers) {
        this(null, null, handlers);
    }

    public BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                            @Nullable BunnyHttpServerConfig httpServerConfig,
                            BunnyHandler<?, ?>... handlers) {
        this(eventBusConfig, httpServerConfig, newArrayList(handlers));
    }

    public BunnyApplication(Iterator<BunnyHandler<?, ?>> handlers) {
        this(null, null, handlers);
    }

    public BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                            @Nullable BunnyHttpServerConfig httpServerConfig,
                            Iterator<BunnyHandler<?, ?>> handlers) {
        this(eventBusConfig, httpServerConfig, newArrayList(handlers));
    }

    public BunnyApplication(Iterable<BunnyHandler<?, ?>> handlers) {
        this(null, null, handlers);
    }

    public BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                            @Nullable BunnyHttpServerConfig httpServerConfig,
                            Iterable<BunnyHandler<?, ?>> handlers) {
        this.eventBusConfig = eventBusConfig;
        this.httpServerConfig = httpServerConfig;
        this.handlers = newArrayList(handlers);
    }

    public <T> BunnyApplication(T[] handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(null, null, handlerParameters, handlerBuilder);
    }

    public <T> BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                                @Nullable BunnyHttpServerConfig httpServerConfig,
                                T[] handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(eventBusConfig, httpServerConfig, newArrayList(handlerParameters), handlerBuilder);
    }

    public <T> BunnyApplication(Iterator<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(null, null, handlerParameters, handlerBuilder);
    }

    public <T> BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                                @Nullable BunnyHttpServerConfig httpServerConfig,
                                Iterator<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(eventBusConfig, httpServerConfig, newArrayList(handlerParameters), handlerBuilder);
    }

    public <T> BunnyApplication(Iterable<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this(null, null, handlerParameters, handlerBuilder);
    }

    public <T> BunnyApplication(@Nullable BunnyEventBusConfig eventBusConfig,
                                @Nullable BunnyHttpServerConfig httpServerConfig,
                                Iterable<T> handlerParameters,
                                Function<T, BunnyHandler<?, ?>> handlerBuilder) {
        this.eventBusConfig = eventBusConfig;
        this.httpServerConfig = httpServerConfig;
        checkNotNull(handlerBuilder);
        this.handlers = newArrayList(handlerParameters).stream()
                .map(handlerBuilder).collect(Collectors.toList());
    }

    public void deploy(Vertx vertx) {
        deploy(vertx, null);
    }

    public void deploy(Vertx vertx, Handler<AsyncResult<BunnyVerticleDeployment>> completer) {
        vertx.deployVerticle(new BunnyEventBusVerticle(this.handlers,
                this.eventBusConfig), wrapHandler(EVENT_BUS_VERTICLE, completer));
        vertx.deployVerticle(new BunnyHttpServerVerticle(this.handlers,
                this.httpServerConfig), wrapHandler(HTTP_SERVER_VERTICLE, completer));
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
