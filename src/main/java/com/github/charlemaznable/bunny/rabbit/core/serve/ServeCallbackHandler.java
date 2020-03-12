package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.ServeCallbackRequest;
import com.github.charlemaznable.bunny.client.domain.ServeCallbackResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.core.net.ohclient.OhReq;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Map;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_FAILURE;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_STANDBY;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_SUCCESS;
import static com.github.charlemaznable.core.codec.Json.json;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Str.isBlank;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class ServeCallbackHandler
        implements BunnyHandler<ServeCallbackRequest, ServeCallbackResponse> {

    private final ServeCallbackPluginLoader pluginLoader;
    private final ServeService serveService;
    private final BunnyCallbackDao bunnyCallbackDao;
    private final BunnyConfig bunnyConfig;

    @Inject
    @Autowired
    public ServeCallbackHandler(ServeCallbackPluginLoader pluginLoader,
                                ServeService serveService,
                                @Nullable BunnyCallbackDao bunnyCallbackDao,
                                @Nullable BunnyConfig bunnyConfig) {
        this.pluginLoader = checkNotNull(pluginLoader);
        this.serveService = checkNotNull(serveService);
        this.bunnyCallbackDao = nullThen(bunnyCallbackDao,
                () -> getEqler(BunnyCallbackDao.class));
        this.bunnyConfig = nullThen(bunnyConfig,
                () -> getMiner(BunnyConfig.class));
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
        serveContext.context = request.getContext();
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
                serveCallbackPlugin.checkRequest(serveContext.context,
                        serveContext.internalRequest, asyncResult -> {
                            if (asyncResult.failed()) {
                                // 判断结果异常 -> 回调检查失败
                                future.fail(asyncResult.cause());
                            } else {
                                // 记录服务下发结果
                                serveContext.resultSuccess = asyncResult.result();
                                future.complete(serveContext);
                            }
                });
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
            if (!TRUE.equals(serveContext.resultSuccess)) {
                // 服务下发未成功 -> 回退预扣减
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
            new CallbackPeriodic(bunnyCallbackDao,
                    bunnyConfig, serveContext).handle(null);
        });
    }

    private static class CallbackPeriodic implements Handler<Long> {

        private BunnyCallbackDao bunnyCallbackDao;
        private BunnyConfig bunnyConfig;
        private Map<String, Object> context;
        private Map<String, Object> request;
        private String chargingType;
        private String seqId;
        private int count = 0;

        public CallbackPeriodic(BunnyCallbackDao bunnyCallbackDao,
                                BunnyConfig bunnyConfig,
                                ServeContext serveContext) {
            this.bunnyCallbackDao = bunnyCallbackDao;
            this.bunnyConfig = bunnyConfig;
            this.context = serveContext.context;
            this.request = serveContext.internalRequest;
            this.chargingType = serveContext.chargingType;
            this.seqId = serveContext.seqId;
        }

        @Override
        public void handle(Long ignored) {
            executeBlocking(context, block -> {
                // 查询回调地址
                val callbackUrl = bunnyCallbackDao.queryCallbackUrl(chargingType, seqId);
                if (isBlank(callbackUrl)) {
                    block.complete();
                    return;
                }
                // 记录回调请求
                bunnyCallbackDao.logCallback(toStr(next()), seqId, "callback-req", json(request));
                // 回调
                val callbackResult = new OhReq(callbackUrl).parameters(request).get();
                // 记录回调响应
                bunnyCallbackDao.logCallback(toStr(next()), seqId, "callback-rsp", callbackResult);
                // 判断回调结果
                count += 1;
                String state;
                boolean finish;
                if ("OK".equals(callbackResult)) {
                    state = CALLBACK_SUCCESS;
                    finish = true;
                } else if (count >= bunnyConfig.callbackLimit()) {
                    state = CALLBACK_FAILURE;
                    finish = true;
                } else {
                    state = CALLBACK_STANDBY;
                    finish = false;
                }
                // 更新回调状态
                bunnyCallbackDao.updateCallbackState(chargingType, seqId, state);
                if (finish) {
                    block.complete();
                } else {
                    block.fail("");
                }
            }, asyncResult -> {
                if (asyncResult.succeeded()) return;
                val vertx = Vertx.currentContext().owner();
                vertx.setTimer(bunnyConfig.callbackDelay(), this);
            });
        }
    }
}
