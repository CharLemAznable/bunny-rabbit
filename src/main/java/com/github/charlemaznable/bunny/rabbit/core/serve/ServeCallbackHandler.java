package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.ServeCallbackRequest;
import com.github.charlemaznable.bunny.client.domain.ServeCallbackResponse;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.core.net.ohclient.OhReq;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_STANDBY;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_SUCCESS;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.nonNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class ServeCallbackHandler
        implements BunnyHandler<ServeCallbackRequest, ServeCallbackResponse> {

    private final ServeCallbackPluginLoader pluginLoader;
    private final ServeService serveService;
    private final BunnyCallbackDao bunnyCallbackDao;

    @Inject
    @Autowired
    public ServeCallbackHandler(ServeCallbackPluginLoader pluginLoader,
                                ServeService serveService,
                                @Nullable BunnyCallbackDao bunnyCallbackDao) {
        this.pluginLoader = checkNotNull(pluginLoader);
        this.serveService = serveService;
        this.bunnyCallbackDao = nullThen(bunnyCallbackDao,
                () -> getEqler(BunnyCallbackDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.SERVE_CALLBACK;
    }

    @Override
    public Class<? extends ServeCallbackRequest> getRequestClass() {
        return ServeCallbackRequest.class;
    }

    @Override
    public void execute(ServeCallbackRequest request,
                        Handler<AsyncResult<ServeCallbackResponse>> handler) {
        val response = request.createResponse();
        val serveContext = buildServeContext(request);

        check(serveContext).compose(this::sufcheck)
                .compose(this::callback).setHandler(async -> {
            if (async.failed()) {
                handler.handle(failedFuture(async.cause()));
                return;
            }

            response.succeed();
            val context = async.result();
            if (nonNull(context.unexpectedThrowable)) {
                response.setUnexpectedFailure(
                        context.unexpectedThrowable.getMessage());
            }
            handler.handle(succeededFuture(response));
        });
    }

    private ServeContext buildServeContext(ServeCallbackRequest request) {
        val serveContext = new ServeContext();
        serveContext.chargingType = request.getChargingType();
        serveContext.serveType = request.getServeType();
        serveContext.internalRequest = newHashMap(request.getInternalRequest());
        serveContext.seqId = request.getSeqId();
        return serveContext;
    }

    /**
     * step1 服务回调检查
     */
    private Future<ServeContext> check(ServeContext serveContext) {
        return Future.future(future -> {
            try {
                val serveCallbackPlugin = pluginLoader.load(serveContext.serveType);
                // 插件判断服务下发结果
                serveContext.resultSuccess = serveCallbackPlugin
                        .checkRequest(serveContext.internalRequest);
                future.complete(serveContext);
            } catch (Exception e) {
                // 插件加载失败|插件抛出异常 -> 回调检查失败
                future.fail(e);
            }
        });
    }

    /**
     * step2 确认/回退预扣减
     */
    private Future<ServeContext> sufcheck(ServeContext serveContext) {
        return Future.future(future -> {
            if (!serveContext.resultSuccess) {
                // 服务下发失败 -> 回退预扣减
                serveService.executeRollback(serveContext, future);
            } else {
                // 服务下发成功 -> 确认预扣减
                serveService.executeCommit(serveContext, future);
            }
        });
    }

    /**
     * step3 触发回调
     */
    private Future<ServeContext> callback(ServeContext serveContext) {
        return Future.future(future -> {
            // 直接返回, 不影响回调响应
            future.complete(serveContext);

            // 异步开启回调
            executeBlocking(block -> {
                // 查询回调地址
                val callbackUrl = bunnyCallbackDao.queryCallbackUrl(
                        serveContext.chargingType, serveContext.seqId);
                if (isBlank(callbackUrl)) {
                    block.complete();
                    return;
                }
                // 记录回调请求
                bunnyCallbackDao.logCallback(toStr(next()),
                        serveContext.seqId, "callback-req",
                        json(serveContext.internalRequest));
                // 回调
                val callbackResult = new OhReq(callbackUrl)
                        .parameters(serveContext.internalRequest).get();
                // 记录回调响应
                bunnyCallbackDao.logCallback(toStr(next()),
                        serveContext.seqId, "callback-rsp", callbackResult);
                // 更新回调状态
                bunnyCallbackDao.updateCallbackState(
                        serveContext.chargingType, serveContext.seqId,
                        "OK".equals(callbackResult) ? CALLBACK_SUCCESS : CALLBACK_STANDBY);
                block.complete();
            }, Promise.promise());
        });
    }
}
