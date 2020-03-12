package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.BunnyException;
import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
import com.github.charlemaznable.bunny.client.domain.ServeRequest;
import com.github.charlemaznable.bunny.client.domain.ServeResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.calculate.CalculateHandler;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.bunny.rabbit.core.wrapper.BunnyElf.failure;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;

@Component
public final class ServeHandler
        implements BunnyHandler<ServeRequest, ServeResponse> {

    private final CalculateHandler calculateHandler;
    private final ServePluginLoader pluginLoader;
    private final ServeService serveService;

    @Inject
    @Autowired
    public ServeHandler(CalculateHandler calculateHandler,
                        ServePluginLoader pluginLoader,
                        ServeService serveService) {
        this.calculateHandler = checkNotNull(calculateHandler);
        this.pluginLoader = checkNotNull(pluginLoader);
        this.serveService = checkNotNull(serveService);
    }

    @Override
    public String address() {
        return BunnyAddress.SERVE;
    }

    @Override
    public Class<? extends ServeRequest> getRequestClass() {
        return ServeRequest.class;
    }

    @Override
    public void execute(ServeRequest request,
                        Handler<AsyncResult<ServeResponse>> handler) {
        val response = request.createResponse();
        val serveContext = buildServeContext(request);

        calculatePaymentValue(serveContext).compose(this::preserve)
                .compose(this::serve).compose(this::sufserve).setHandler(async -> {
            if (async.failed()) {
                handler.handle(failedFuture(async.cause()));
                return;
            }

            val context = async.result();
            if (!context.returnSuccess) {
                // 服务调用失败, 返回异常信息
                var failure = failure(context.internalThrowable);
                if (nonNull(context.unexpectedThrowable)) {
                    val unexpected = failure(context.unexpectedThrowable);
                    failure = new BunnyException(
                            failure.respCode() + "(" + unexpected.respCode() + ")",
                            failure.respDesc() + "(" + unexpected.respDesc() + ")");
                }
                handler.handle(failedFuture(failure));
                return;
            }

            // 服务调用成功, 返回服务响应
            response.succeed();
            response.setInternalResponse(
                    context.internalResponse);
            if (nonNull(context.unexpectedThrowable)) {
                response.setUnexpectedFailure(
                        context.unexpectedThrowable.getMessage());
            }
            handler.handle(succeededFuture(response));
        });
    }

    private ServeContext buildServeContext(ServeRequest request) {
        val serveContext = new ServeContext();
        serveContext.chargingType = request.getChargingType();
        serveContext.context = request.getContext();
        serveContext.paymentValue = request.getPaymentValue();
        serveContext.serveType = request.getServeType();
        serveContext.internalRequest = newHashMap(request.getInternalRequest());
        serveContext.callbackUrl = request.getCallbackUrl();
        return serveContext;
    }

    /**
     * step1 服务计费
     */
    private Future<ServeContext> calculatePaymentValue(ServeContext serveContext) {
        return Future.future(future -> {
            if (nonNull(serveContext.paymentValue)) {
                future.complete(serveContext);
                return;
            }

            val calculateRequest = new CalculateRequest();
            calculateRequest.setChargingType(serveContext.chargingType);
            calculateRequest.getContext().putAll(serveContext.context);
            calculateRequest.setChargingParameters(serveContext.internalRequest);
            calculateHandler.execute(calculateRequest, asyncResult -> {
                if (asyncResult.failed()) {
                    future.fail(asyncResult.cause());
                    return;
                }
                val calculateResponse = asyncResult.result();
                serveContext.paymentValue = calculateResponse.getCalculate();
                future.complete(serveContext);
            });
        });
    }

    /**
     * step2 预扣减
     */
    private Future<ServeContext> preserve(ServeContext serveContext) {
        return Future.future(future -> serveService
                .executePreserve(serveContext, future));
    }

    /**
     * step3 服务调用
     */
    private Future<ServeContext> serve(ServeContext serveContext) {
        return Future.future(future -> {
            try {
                val servePlugin = pluginLoader.load(serveContext.serveType);
                val context = serveContext.context;
                val internalRequest = serveContext.internalRequest;
                servePlugin.serve(context, internalRequest, asyncServe -> {
                    if (asyncServe.failed()) {
                        // 插件回调失败 -> 服务调用失败
                        serveContext.returnSuccess = false;
                        serveContext.internalThrowable = asyncServe.cause();
                        future.complete(serveContext);
                        return;
                    }
                    // 插件调用成功 -> 服务调用成功
                    serveContext.returnSuccess = true;
                    serveContext.internalResponse = asyncServe.result();

                    // 插件判断服务下发结果
                    servePlugin.checkResponse(context, serveContext.internalResponse, asyncCheck -> {
                        if (asyncCheck.failed()) {
                            // 判断结果异常 -> 服务调用失败
                            serveContext.returnSuccess = false;
                            serveContext.internalResponse = null;
                            serveContext.internalThrowable = asyncCheck.cause();
                        } else {
                            // 记录服务下发结果
                            serveContext.resultSuccess = asyncCheck.result();
                        }
                        future.complete(serveContext);
                    });
                });
            } catch (Exception e) {
                // 插件加载失败|插件抛出异常 -> 服务调用失败
                serveContext.returnSuccess = false;
                serveContext.internalThrowable = e;
                future.complete(serveContext);
            }
        });
    }

    /**
     * step4 确认/回退预扣减
     */
    private Future<ServeContext> sufserve(ServeContext serveContext) {
        return Future.future(future -> {
            if (!serveContext.returnSuccess ||
                    FALSE.equals(serveContext.resultSuccess)) {
                // 服务调用失败 -> 回退预扣减
                // 服务下发失败 -> 回退预扣减
                serveService.executeRollback(serveContext, future);
                return;
            }
            if (TRUE.equals(serveContext.resultSuccess)) {
                // 服务调用成功, 服务下发成功 -> 确认预扣减
                serveService.executeCommit(serveContext, future);
                return;
            }
            // 服务调用成功, 服务下发未成功(resultSuccess==null) -> 返回, 等待回调
            future.complete(serveContext);
        });
    }
}
