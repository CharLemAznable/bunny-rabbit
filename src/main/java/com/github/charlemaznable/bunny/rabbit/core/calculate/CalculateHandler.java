package com.github.charlemaznable.bunny.rabbit.core.calculate;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
import com.github.charlemaznable.bunny.client.domain.CalculateResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.core.common.CalculatePluginLoader;
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

    private final CalculatePluginLoader calculatePluginLoader;

    @Inject
    @Autowired
    public CalculateHandler(CalculatePluginLoader calculatePluginLoader) {
        this.calculatePluginLoader = checkNotNull(calculatePluginLoader);
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
        val serveName = request.getServeName();
        val context = request.getContext();
        val parameters = request.getChargingParameters();

        try {
            val calculatePlugin = calculatePluginLoader.load(serveName);
            calculatePlugin.calculate(context, parameters, async -> {
                if (async.failed()) {
                    handler.handle(failedFuture(async.cause()));
                    return;
                }

                response.setCalculate(async.result());
                response.succeed();
                handler.handle(succeededFuture(response));
            });
        } catch (Exception e) {
            handler.handle(failedFuture(e));
        }
    }
}
