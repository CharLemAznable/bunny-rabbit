package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.PaymentRollbackRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentRollbackResponse;
import com.github.charlemaznable.bunny.rabbit.handler.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.handler.dao.BunnyRollbackDao;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.ROLLBACK_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.ROLLBACK_QUERY_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Slf4j
@Component
public final class PaymentRollbackHandler
        implements BunnyHandler<PaymentRollbackRequest, PaymentRollbackResponse> {

    private final BunnyDao bunnyDao;

    public PaymentRollbackHandler() {
        this(null);
    }

    @Inject
    @Autowired
    public PaymentRollbackHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.PAYMENT_ROLLBACK;
    }

    @Override
    public Class<PaymentRollbackRequest> getRequestClass() {
        return PaymentRollbackRequest.class;
    }

    @Override
    public void execute(PaymentRollbackRequest request,
                        Handler<AsyncResult<PaymentRollbackResponse>> handler) {
        executeBlocking(future -> {
            val chargingType = request.getChargingType();
            val paymentId = request.getPaymentId();

            @Cleanup val rollbackDao = getEqler(BunnyRollbackDao.class);
            try {
                val response = new PaymentRollbackResponse();
                response.setChargingType(chargingType);
                val result = bunnyDao.queryPaymentSequence(chargingType, paymentId);
                if (isNull(result)) {
                    response.setRespCode(ROLLBACK_QUERY_FAILED.respCode());
                    response.setRespDesc(ROLLBACK_QUERY_FAILED.respDesc());
                    future.complete(response);
                    return;
                }

                rollbackDao.start();
                val update = rollbackDao.updateBalanceByRollback(chargingType, result.getUsed());
                val rollback = rollbackDao.rollbackPaymentSequence(chargingType, paymentId);
                if (update != 1 || rollback != 1) {
                    rollbackDao.rollback();
                    response.setRespCode(ROLLBACK_FAILED.respCode());
                    response.setRespDesc(ROLLBACK_FAILED.respDesc());
                    future.complete(response);
                    return;
                }

                rollbackDao.commit();
                response.setRespCode(RESP_CODE_OK);
                response.setRespDesc(RESP_DESC_SUCCESS);
                response.setRollback(result.getUsed());
                response.setUnit(result.getUnit());
                future.complete(response);

            } catch (Exception e) {
                log.warn("Payment Rollback of chargingType:{} failed:\n", chargingType, e);
                future.fail(e);
            }
        }, handler);
    }
}