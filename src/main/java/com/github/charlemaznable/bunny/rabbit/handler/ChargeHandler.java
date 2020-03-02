package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.ChargeRequest;
import com.github.charlemaznable.bunny.client.domain.ChargeResponse;
import com.github.charlemaznable.bunny.rabbit.handler.dao.BunnyDao;
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
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.CHARGE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Slf4j
@Component
public final class ChargeHandler
        implements BunnyHandler<ChargeRequest, ChargeResponse> {

    private final BunnyDao bunnyDao;

    public ChargeHandler() {
        this(null);
    }

    @Inject
    @Autowired
    public ChargeHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.CHARGE;
    }

    @Override
    public Class<ChargeRequest> getRequestClass() {
        return ChargeRequest.class;
    }

    @Override
    public void execute(ChargeRequest request,
                        Handler<AsyncResult<ChargeResponse>> handler) {
        executeBlocking(future -> {
            val chargingType = request.getChargingType();
            val chargeValue = request.getChargeValue();

            try {
                val response = new ChargeResponse();
                response.setChargingType(chargingType);
                val result = bunnyDao.updateBalanceByCharge(chargingType, chargeValue);
                if (result == 1) {
                    response.setRespCode(RESP_CODE_OK);
                    response.setRespDesc(RESP_DESC_SUCCESS);
                } else {
                    response.setRespCode(CHARGE_FAILED.respCode());
                    response.setRespDesc(CHARGE_FAILED.respDesc());
                }
                future.complete(response);

            } catch (Exception e) {
                log.warn("Balance charging:{} failed:\n", chargingType, e);
                future.fail(e);
            }
        }, handler);
    }
}
