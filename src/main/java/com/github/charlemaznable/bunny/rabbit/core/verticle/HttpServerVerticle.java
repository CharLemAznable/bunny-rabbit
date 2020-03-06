package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.wrapper.HttpServerHandlerWrapper;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.Nullable;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;

@Slf4j
public final class HttpServerVerticle extends BunnyAbstractVerticle {

    public static final String HTTP_SERVER_VERTICLE = "BUNNY_HTTP_SERVER_VERTICLE";

    public HttpServerVerticle(@Nullable BunnyConfig bunnyConfig,
                              @Nullable BunnyLogDao bunnyLogDao,
                              List<BunnyHandler> handlers) {
        super(bunnyConfig, bunnyLogDao, handlers);
    }

    @Override
    public void start(Future<Void> startFuture) {
        val bunnyRouter = buildBunnyRouter();
        val rootRouter = buildRootRouter(bunnyRouter);
        vertx.createHttpServer().requestHandler(rootRouter)
                .listen(bunnyConfig.port(), result -> {
                    log.info("Bunny Http Server start: " +
                            (result.succeeded() ? "SUCCESS" : "FAILED"));
                    startFuture.handle(result.mapEmpty());
                });
    }

    @SuppressWarnings("unchecked")
    private Router buildBunnyRouter() {
        val bunnyRouter = Router.router(vertx);
        bunnyRouter.route(HttpMethod.POST, "/*")
                .handler(BodyHandler.create(false))
                .produces("application/json")
                .handler(ResponseContentTypeHandler.create());

        for (val handler : handlers) {
            val address = prependIfMissing(handler.address(), "/");
            bunnyRouter.route(address).handler(new HttpServerHandlerWrapper<>(
                    handler, bunnyConfig.interceptors(), bunnyLogDao));
        }
        return bunnyRouter;
    }

    private Router buildRootRouter(Router bunnyRouter) {
        val contextPath = prependIfMissing(bunnyConfig.contextPath(), "/");
        if ("/".equals(contextPath)) return bunnyRouter;
        return Router.router(vertx).mountSubRouter(contextPath, bunnyRouter);
    }
}