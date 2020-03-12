package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.NonsenseSignature;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.REQUEST_BODY_ERROR;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.spec;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse, R>
        implements Handler<R> {

    private final BunnyHandler<T, U> bunnyHandler;
    private final BunnyLogDao bunnyLogDao;
    private final NonsenseSignature nonsenseSignature = new NonsenseSignature();

    public BunnyHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                               @Nullable BunnyLogDao bunnyLogDao) {
        this.bunnyHandler = checkNotNull(bunnyHandler);
        this.bunnyLogDao = nullThen(bunnyLogDao,
                () -> getEqler(BunnyLogDao.class));
    }

    @Override
    public final void handle(R event) {
        val requestBody = produceRequest(event);
        if (isBlank(requestBody)) {
            val throwable = REQUEST_BODY_ERROR.exception("Body is Blank");
            consumeError(event, throwable);
            return;
        }
        val requestMap = unJson(requestBody);
        if (!nonsenseSignature.verify(requestMap)) {
            val throwable = REQUEST_BODY_ERROR.exception("Verify Failed");
            consumeError(event, throwable);
            return;
        }

        val request = spec(requestMap, bunnyHandler.getRequestClass());
        asyncLog(request, "request", requestBody);

        bunnyHandler.execute(request, asyncResult -> {
            if (asyncResult.failed()) {
                val throwable = asyncResult.cause();
                consumeError(event, throwable);
                asyncLog(request, "response", throwable.getMessage());
                return;
            }

            U response = asyncResult.result();
            val responseMap = nonsenseSignature.sign(response);
            val responseBody = json(responseMap);
            consumeResponse(event, responseBody);
            asyncLog(request, "response", responseBody);
        });
    }

    public abstract String produceRequest(R event);

    public abstract void consumeError(R event, Throwable throwable);

    public abstract void consumeResponse(R event, String response);

    private void asyncLog(T request, String logType, String logContent) {
        executeBlocking(request.getContext(), block -> {
            bunnyLogDao.log(toStr(next()),
                    bunnyHandler.address(),
                    logType, logContent);
            block.complete();
        }, Promise.<Void>promise());
    }
}
