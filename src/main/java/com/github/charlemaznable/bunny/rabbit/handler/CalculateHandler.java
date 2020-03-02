package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.CalculateRequest;
import com.github.charlemaznable.bunny.client.domain.CalculateResponse;
import com.github.charlemaznable.bunny.rabbit.config.handler.PluginNameMapper;
import com.github.charlemaznable.bunny.rabbit.handler.plugin.CalculatePluginLoader;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyException;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static java.util.Objects.isNull;

@Slf4j
@Component
public final class CalculateHandler
        implements BunnyHandler<CalculateRequest, CalculateResponse> {

    private final CalculatePluginLoader pluginLoader;
    private final PluginNameMapper pluginNameMapper;

    public CalculateHandler(CalculatePluginLoader pluginLoader) {
        this(pluginLoader, null);
    }

    @Inject
    @Autowired
    public CalculateHandler(CalculatePluginLoader pluginLoader,
                            @Nullable PluginNameMapper pluginNameMapper) {
        this.pluginLoader = checkNotNull(pluginLoader);
        this.pluginNameMapper = nullThen(pluginNameMapper,
                () -> getMiner(PluginNameMapper.class));
    }

    @Override
    public String address() {
        return BunnyAddress.CALCULATE;
    }

    @Override
    public Class<CalculateRequest> getRequestClass() {
        return CalculateRequest.class;
    }

    @Override
    public void execute(CalculateRequest request,
                        Handler<AsyncResult<CalculateResponse>> handler) {
        executeBlocking(future -> {
            val chargingType = request.getChargingType();
            val chargingParameters = request.getChargingParameters();

            val pluginName = pluginNameMapper.pluginName(chargingType);
            if (isNull(pluginName)) {
                log.warn("Calculate Plugin config:{} not found", chargingType);
                future.fail(new BunnyException("ILLEGAL_CHARGING_TYPE",
                        "Calculate Plugin config:" + chargingType + " not found"));
                return;
            }

            val calculatePlugin = pluginLoader.load(pluginName);
            if (isNull(calculatePlugin)) {
                log.warn("Calculate Plugin:{} not found", pluginName);
                future.fail(new BunnyException("ILLEGAL_CHARGING_TYPE",
                        "Calculate Plugin:" + pluginName + " not found"));
                return;
            }

            try {
                val response = new CalculateResponse();
                response.setChargingType(chargingType);
                val result = calculatePlugin.calculate(chargingParameters);
                if (result.isSuccess()) {
                    response.setRespCode(RESP_CODE_OK);
                    response.setRespDesc(RESP_DESC_SUCCESS);
                    response.setCalculate(result.getCalculate());
                    response.setUnit(result.getUnit());
                } else {
                    response.setRespCode(result.getFailCode());
                    response.setRespDesc(result.getFailDesc());
                }
                future.complete(response);

            } catch (Exception e) {
                log.warn("Calculate Plugin:{} calculate failed:\n" +
                                "\t\tchargingType:{} parameters:{}\n",
                        pluginName, chargingType, chargingParameters, e);
                future.fail(e);
            }
        }, handler);
    }
}
