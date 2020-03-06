package com.github.charlemaznable.bunny.rabbit.core.calcute;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
import com.github.charlemaznable.bunny.client.domain.CalculateResponse;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

@Component
public final class CalculateHandler
        implements BunnyHandler<CalculateRequest, CalculateResponse> {

    private final CalculatePluginLoader pluginLoader;

    @Inject
    @Autowired
    public CalculateHandler(CalculatePluginLoader pluginLoader) {
        this.pluginLoader = checkNotNull(pluginLoader);
    }

    @Override
    public String address() {
        return BunnyAddress.CALCULATE;
    }

    @Override
    public Class<? extends CalculateRequest> getRequestClass() {
        return CalculateRequest.class;
    }

    @Override
    public void execute(CalculateRequest request,
                        Handler<AsyncResult<CalculateResponse>> handler) {
        val response = request.createResponse();
        val chargingType = request.getChargingType();
        val chargingParameters = request.getChargingParameters();

        try {
            val calculatePlugin = pluginLoader.load(chargingType);
            calculatePlugin.calculate(chargingParameters, async -> {
                if (async.failed()) {
                    handler.handle(failedFuture(async.cause()));
                    return;
                }

                val result = async.result();
                response.setCalculate(result.getCalculate());
                response.setUnit(result.getUnit());
                response.succeed();
                handler.handle(succeededFuture(response));
            });
        } catch (Exception e) {
            handler.handle(failedFuture(e));
        }
    }
}
