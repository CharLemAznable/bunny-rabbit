package com.github.charlemaznable.bunny.rabbit.core.charge;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.ChargeRequest;
import com.github.charlemaznable.bunny.client.domain.ChargeResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CHARGE_FAILED;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public final class ChargeHandler
        implements BunnyHandler<ChargeRequest, ChargeResponse> {

    private final ChargeCodeMapper chargeCodeMapper;
    private final BunnyDao bunnyDao;

    public ChargeHandler(@Nullable ChargeCodeMapper chargeCodeMapper,
                         @Nullable BunnyDao bunnyDao) {
        this.chargeCodeMapper = nullThen(chargeCodeMapper, () -> getConfig(ChargeCodeMapper.class));
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
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
        val serveName = request.getServeName();
        val chargeCode = chargeCodeMapper.chargeCode(serveName);
        val chargeValue = request.getChargeValue();

        executeBlocking(request.getContext(), block -> {
            val result = bunnyDao.updateBalanceByCharge(
                    chargeCode, chargeValue);
            if (1 != result) {
                block.fail(CHARGE_FAILED.exception());
                return;
            }
            response.succeed();
            block.complete(response);
        }, handler);
    }
}
