package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf.failureMessage;

public final class HttpServerHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
        extends BunnyHandlerWrapper<T, U, RoutingContext> {

    public HttpServerHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                                    @Nullable BunnyConfig bunnyConfig,
                                    @Nullable BunnyLogDao bunnyLogDao,
                                    @Nullable NonsenseOptions nonsenseOptions,
                                    @Nullable SignatureOptions signatureOptions) {
        super(bunnyHandler, bunnyConfig, bunnyLogDao, nonsenseOptions, signatureOptions);
    }

    @Override
    public String produceRequest(RoutingContext routingContext) {
        return routingContext.body().asString();
    }

    @Override
    public void consumeError(RoutingContext routingContext, Throwable throwable) {
        routingContext.response().end(failureMessage(throwable));
    }

    @Override
    public void consumeResponse(RoutingContext routingContext, String response) {
        routingContext.response().end(response);
    }
}
