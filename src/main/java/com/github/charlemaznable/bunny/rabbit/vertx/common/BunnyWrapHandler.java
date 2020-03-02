package com.github.charlemaznable.bunny.rabbit.vertx.common;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.core.codec.NonsenseSignature;
import io.vertx.core.Handler;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyError.REQUEST_BODY_ERROR;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.spec;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Str.isBlank;

@RequiredArgsConstructor
public abstract class BunnyWrapHandler<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse, R>
        implements Handler<R> {

    private final BunnyHandler<T, U> bunnyHandler;
    private NonsenseSignature nonsenseSignature = new NonsenseSignature();

    @Override
    public final void handle(R event) {
        val requestBody = produceRequest(event);
        if (isBlank(requestBody)) {
            consumeError(event, REQUEST_BODY_ERROR
                    .exception("Body is Blank"));
            return;
        }
        val requestMap = unJson(requestBody);
        if (!nonsenseSignature.verify(requestMap)) {
            consumeError(event, REQUEST_BODY_ERROR
                    .exception("Verify Failed"));
            return;
        }

        val request = spec(requestMap, bunnyHandler.getRequestClass());
        bunnyHandler.execute(request, asyncResult -> {
            if (asyncResult.failed()) {
                consumeError(event, asyncResult.cause());
                return;
            }

            val responseMap = nonsenseSignature.sign(asyncResult.result());
            consumeResponse(event, json(responseMap));
        });
    }

    public abstract String produceRequest(R event);

    public abstract void consumeError(R event, Throwable throwable);

    public abstract void consumeResponse(R event, String response);
}
