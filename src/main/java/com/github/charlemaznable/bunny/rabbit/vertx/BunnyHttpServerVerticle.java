package com.github.charlemaznable.bunny.rabbit.vertx;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.rabbit.config.vertx.BunnyHttpServerConfig;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyWrapHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyElf.failureMessage;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static org.apache.commons.lang3.StringUtils.prependIfMissing;

@Slf4j
public final class BunnyHttpServerVerticle extends AbstractVerticle {

    public static final String HTTP_SERVER_VERTICLE = "BUNNY_HTTP_SERVER_VERTICLE";
    private final Iterable<BunnyHandler<?, ?>> handlers;
    private final BunnyHttpServerConfig config;

    public BunnyHttpServerVerticle(Iterable<BunnyHandler<?, ?>> handlers) {
        this.handlers = newArrayList(handlers);
        this.config = getMiner(BunnyHttpServerConfig.class);
    }

    @Override
    public void start(Future<Void> startFuture) {
        val bunnyRouter = buildBunnyRouter(config);
        val rootRouter = buildRootRouter(config, bunnyRouter);

        vertx.createHttpServer().requestHandler(rootRouter)
                .listen(config.port(), result -> {
                    log.info("Bunny Http Server start: " +
                            (result.succeeded() ? "SUCCESS" : "FAILED"));
                    startFuture.handle(result.mapEmpty());
                });
    }

    private Router buildBunnyRouter(BunnyHttpServerConfig config) {
        val bunnyRouter = Router.router(vertx);
        bunnyRouter.route(HttpMethod.POST, "/*")
                .handler(BodyHandler.create(false))
                .produces("application/json")
                .handler(ResponseContentTypeHandler.create())
                .failureHandler(rc -> rc.response().end(
                        failureMessage(rc.failure())));

        for (val handler : handlers) {
            val address = prependIfMissing(handler.address(), "/");
            bunnyRouter.route(address).handler(
                    new BunnyHttpServerHandler<>(handler, config.interceptors()));
        }
        return bunnyRouter;
    }

    private Router buildRootRouter(BunnyHttpServerConfig config, Router bunnyRouter) {
        val contextPath = prependIfMissing(config.contextPath(), "/");
        if ("/".equals(contextPath)) return bunnyRouter;
        return Router.router(vertx).mountSubRouter(contextPath, bunnyRouter);
    }

    static final class BunnyHttpServerHandler<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
            extends BunnyWrapHandler<T, U, RoutingContext> {

        private final List<BunnyHttpServerInterceptor> interceptors;

        public BunnyHttpServerHandler(BunnyHandler<T, U> bunnyHandler,
                                      List<BunnyHttpServerInterceptor> interceptors) {
            super(bunnyHandler);
            this.interceptors = newArrayList(interceptors);
        }

        @Override
        public String produceRequest(RoutingContext routingContext) {
            interceptors.forEach(interceptor ->
                    interceptor.preHandle(routingContext));
            return routingContext.getBodyAsString();
        }

        @Override
        public void consumeError(RoutingContext routingContext, Throwable throwable) {
            routingContext.fail(throwable);
            iterateReverse(routingContext, null, throwable);
        }

        @Override
        public void consumeResponse(RoutingContext routingContext, String response) {
            routingContext.response().end(response);
            iterateReverse(routingContext, response, null);
        }

        private void iterateReverse(RoutingContext routingContext,
                                    @Nullable String response,
                                    @Nullable Throwable throwable) {
            if (interceptors.isEmpty()) return;

            val iterator = interceptors.listIterator(interceptors.size());
            while (iterator.hasPrevious()) {
                val interceptor = iterator.previous();
                interceptor.afterCompletion(routingContext, response, throwable);
            }
        }
    }
}
