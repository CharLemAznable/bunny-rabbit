package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.core.wrapper.HttpServerHandlerWrapper;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.annotation.Nullable;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;

@Slf4j
public final class HttpServerVerticle extends BunnyAbstractVerticle {

    public static final String HTTP_SERVER_VERTICLE = "BUNNY_HTTP_SERVER_VERTICLE";

    public HttpServerVerticle(List<BunnyHandler> handlers,
                              @Nullable BunnyConfig bunnyConfig,
                              @Nullable BunnyLogDao bunnyLogDao,
                              @Nullable NonsenseOptions nonsenseOptions,
                              @Nullable SignatureOptions signatureOptions) {
        super(handlers, bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        val bunnyRouter = buildBunnyRouter();
        val rootRouter = buildRootRouter(bunnyRouter);
        vertx.createHttpServer().requestHandler(rootRouter)
                .listen(bunnyConfig.port(), result -> {
                    log.info("Bunny Http Server start:{} {}", bunnyConfig.port(),
                            (result.succeeded() ? "SUCCESS" : "FAILED"));
                    startPromise.handle(result.mapEmpty());
                });
    }

    @SuppressWarnings("unchecked")
    private Router buildBunnyRouter() {
        val bunnyRouter = Router.router(vertx);
        bunnyRouter.route(HttpMethod.POST, "/*")
                .handler(BodyHandler.create(false))
                .produces("application/json");

        for (val handler : handlers) {
            val address = prependIfMissing(handler.address(), "/");
            val wrapper = new HttpServerHandlerWrapper(handler,
                    bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
            bunnyRouter.route(address).handler(wrapper);
        }
        return bunnyRouter;
    }

    private Router buildRootRouter(Router bunnyRouter) {
        val contextPath = prependIfMissing(bunnyConfig.contextPath(), "/");
        if ("/".equals(contextPath)) return bunnyRouter;

        val router = Router.router(vertx);
        router.route(contextPath + "*").subRouter(bunnyRouter);
        return router;
    }
}
