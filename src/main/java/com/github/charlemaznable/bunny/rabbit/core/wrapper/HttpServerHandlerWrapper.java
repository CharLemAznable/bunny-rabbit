package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf.failureMessage;

public final class HttpServerHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse>
        extends BunnyHandlerWrapper<T, U, RoutingContext> {

    public HttpServerHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                                    List<BunnyInterceptor> interceptors,
                                    @Nullable BunnyLogDao bunnyLogDao) {
        super(bunnyHandler, interceptors, bunnyLogDao);
    }

    @Override
    public String produceRequest(RoutingContext routingContext) {
        return routingContext.getBodyAsString();
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
