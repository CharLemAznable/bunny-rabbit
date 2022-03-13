package com.github.charlemaznable.bunny.rabbit.core.wrapper;

import com.github.charlemaznable.bunny.client.domain.BunnyBaseRequest;
import com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.core.codec.NonsenseSignature;
import com.github.charlemaznable.core.codec.nonsense.NonsenseOptions;
import com.github.charlemaznable.core.codec.signature.SignatureOptions;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.val;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.REQUEST_BODY_ERROR;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.codec.Json.spec;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.notNullThen;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public abstract class BunnyHandlerWrapper<T extends BunnyBaseRequest<U>, U extends BunnyBaseResponse, R>
        implements Handler<R> {

    private final BunnyHandler<T, U> bunnyHandler;
    private final BunnyConfig bunnyConfig;
    private final BunnyLogDao bunnyLogDao;
    private final NonsenseSignature nonsenseSignature;

    public BunnyHandlerWrapper(BunnyHandler<T, U> bunnyHandler,
                               @Nullable BunnyConfig bunnyConfig,
                               @Nullable BunnyLogDao bunnyLogDao,
                               @Nullable NonsenseOptions nonsenseOptions,
                               @Nullable SignatureOptions signatureOptions) {
        this.bunnyHandler = checkNotNull(bunnyHandler);
        this.bunnyConfig = nullThen(bunnyConfig, () -> getConfig(BunnyConfig.class));
        this.bunnyLogDao = nullThen(bunnyLogDao, () -> getEqler(BunnyLogDao.class));
        this.nonsenseSignature = new NonsenseSignature();
        notNullThen(nonsenseOptions, this.nonsenseSignature::nonsenseOptions);
        notNullThen(signatureOptions, this.nonsenseSignature::signatureOptions);
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
        @SuppressWarnings("unchecked")
        val context = newHashMap((Map<String, Object>) requestMap.get("context"));
        val acceptContextKeyList = bunnyConfig.acceptContextKeyList();
        requestMap.put("context", context.entrySet().stream()
                .filter(e -> acceptContextKeyList.contains(e.getKey()))
                .collect(HashMap::new, (m, e) ->
                        m.put(e.getKey(), e.getValue()), Map::putAll));

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
