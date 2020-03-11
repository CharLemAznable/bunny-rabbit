package com.github.charlemaznable.bunny.rabbit.core.charge;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.ChargeRequest;
import com.github.charlemaznable.bunny.client.domain.ChargeResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CHARGE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf.executeBlocking;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class ChargeHandler
        implements BunnyHandler<ChargeRequest, ChargeResponse> {

    private final BunnyDao bunnyDao;

    @Inject
    @Autowired
    public ChargeHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao,
                () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.CHARGE;
    }

    @Override
    public Class<? extends ChargeRequest> getRequestClass() {
        return ChargeRequest.class;
    }

    @Override
    public void execute(ChargeRequest request,
                        Handler<AsyncResult<ChargeResponse>> handler) {
        val response = request.createResponse();
        val chargingType = request.getChargingType();
        val chargeValue = request.getChargeValue();

        executeBlocking(block -> {
            val result = bunnyDao.updateBalanceByCharge(
                    chargingType, chargeValue);
            if (1 != result) {
                block.fail(CHARGE_FAILED.exception());
                return;
            }
            response.succeed();
            block.complete(response);
        }, handler);
    }
}
