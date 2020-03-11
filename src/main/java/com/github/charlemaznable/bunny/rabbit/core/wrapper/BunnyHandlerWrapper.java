package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.plugin.BunnyInterceptor;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.NonsenseSignature;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.val;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.REQUEST_BODY_ERROR;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf.executeBlocking;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.spec;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse, R>
        implements Handler<R> {

    private final BunnyHandler<T, U> bunnyHandler;
    private final List<BunnyInterceptor> interceptors;
    private final BunnyLogDao bunnyLogDao;
    private final NonsenseSignature nonsenseSignature = new NonsenseSignature();

    public BunnyHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                               List<BunnyInterceptor> interceptors,
                               @Nullable BunnyLogDao bunnyLogDao) {
        this.bunnyHandler = checkNotNull(bunnyHandler);
        this.interceptors = newArrayList(interceptors);
        this.bunnyLogDao = nullThen(bunnyLogDao,
                () -> getEqler(BunnyLogDao.class));
    }

    @Override
    public final void handle(R event) {
        val requestBody = produceRequestWrap(event);
        if (isBlank(requestBody)) {
            val throwable = REQUEST_BODY_ERROR.exception("Body is Blank");
            consumeErrorWrap(event, throwable);
            afterCompletion(null, null, throwable);
            return;
        }
        val requestMap = unJson(requestBody);
        if (!nonsenseSignature.verify(requestMap)) {
            val throwable = REQUEST_BODY_ERROR.exception("Verify Failed");
            consumeErrorWrap(event, throwable);
            afterCompletion(null, null, throwable);
            return;
        }

        val request = spec(requestMap, bunnyHandler.getRequestClass());
        preHandle(request);

        bunnyHandler.execute(request, asyncResult -> {
            if (asyncResult.failed()) {
                val throwable = asyncResult.cause();
                consumeErrorWrap(event, throwable);
                afterCompletion(request, null, throwable);
                return;
            }

            U response = asyncResult.result();
            val responseMap = nonsenseSignature.sign(response);
            consumeResponseWrap(event, json(responseMap));
            afterCompletion(request, response, null);
        });
    }

    public abstract String produceRequest(R event);

    public abstract void consumeError(R event, Throwable throwable);

    public abstract void consumeResponse(R event, String response);

    public String produceRequestWrap(R event) {
        val requestBody = produceRequest(event);
        asyncLog("request", requestBody);
        return requestBody;
    }

    public void consumeErrorWrap(R event, Throwable throwable) {
        asyncLog("response", throwable.getMessage());
        consumeError(event, throwable);
    }

    public void consumeResponseWrap(R event, String response) {
        asyncLog("response", response);
        consumeResponse(event, response);
    }

    private void asyncLog(String logType, String logContent) {
        executeBlocking(block -> {
            bunnyLogDao.log(toStr(next()),
                    bunnyHandler.address(),
                    logType, logContent);
            block.complete();
        }, Promise.<Void>promise());
    }

    private void preHandle(T request) {
        if (interceptors.isEmpty()) return;
        interceptors.forEach(interceptor -> interceptor.preHandle(request));
    }

    private void afterCompletion(@Nullable T request,
                                 @Nullable U response,
                                 @Nullable Throwable throwable) {
        if (interceptors.isEmpty()) return;
        val iterator = interceptors.listIterator(interceptors.size());
        while (iterator.hasPrevious()) {
            val interceptor = iterator.previous();
            interceptor.afterCompletion(request, response, throwable);
        }
    }
}
