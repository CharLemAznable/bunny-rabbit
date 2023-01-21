package com.github.charlemaznable.bunny.rabbit.core;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerLoader;
import com.github.charlemaznable.bunny.rabbit.core.verticle.EventBusVerticle;
import com.github.charlemaznable.bunny.rabbit.core.verticle.HttpServerVerticle;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.verticle.EventBusVerticle.EVENT_BUS_VERTICLE;
import static com.github.charlemaznable.bunny.rabbit.core.verticle.HttpServerVerticle.HTTP_SERVER_VERTICLE;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

@Slf4j
public final class BunnyVertxApplication {

    private final Vertx vertx;
    private final BunnyHandlerLoader handlerLoader;
    private final BunnyConfig bunnyConfig;
    private final BunnyLogDao bunnyLogDao;
    private final NonsenseOptions nonsenseOptions;
    private final SignatureOptions signatureOptions;

    public BunnyVertxApplication(Vertx vertx,
                                 BunnyHandlerLoader handlerLoader,
                                 @Nullable BunnyConfig bunnyConfig,
                                 @Nullable BunnyLogDao bunnyLogDao,
                                 @Nullable NonsenseOptions nonsenseOptions,
                                 @Nullable SignatureOptions signatureOptions) {
        this.vertx = checkNotNull(vertx);
        this.handlerLoader = checkNotNull(handlerLoader);
        this.bunnyConfig = bunnyConfig;
        this.bunnyLogDao = bunnyLogDao;
        this.nonsenseOptions = nonsenseOptions;
        this.signatureOptions = signatureOptions;
    }

    public void deploy(@Nullable Handler<AsyncResult<BunnyVerticleDeployment>> completer) {
        val handlers = handlerLoader.loadHandlers();
        val eventBusVerticle = new EventBusVerticle(handlers,
                bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
        val httpServerVerticle = new HttpServerVerticle(handlers,
                bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
        vertx.deployVerticle(eventBusVerticle, wrapHandler(EVENT_BUS_VERTICLE, completer));
        vertx.deployVerticle(httpServerVerticle, wrapHandler(HTTP_SERVER_VERTICLE, completer));
    }

    private Handler<AsyncResult<String>> wrapHandler(
            String verticleName, Handler<AsyncResult<BunnyVerticleDeployment>> completer) {
        return arDeployment -> {
            if (null == completer) return;

            if (arDeployment.failed()) {
                log.warn("Verticle:{} deploy failed:",
                        verticleName, arDeployment.cause());
            }
            completer.handle(Future.succeededFuture(
                    new BunnyVerticleDeployment(verticleName, arDeployment.result())));
        };
    }
}
