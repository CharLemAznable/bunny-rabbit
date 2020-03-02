package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.PaymentCommitRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentCommitResponse;
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
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_QUERY_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Slf4j
@Component
public final class PaymentCommitHandler
        implements BunnyHandler<PaymentCommitRequest, PaymentCommitResponse> {

    private final BunnyDao bunnyDao;

    public PaymentCommitHandler() {
        this(null);
    }

    @Inject
    @Autowired
    public PaymentCommitHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.PAYMENT_COMMIT;
    }

    @Override
    public Class<PaymentCommitRequest> getRequestClass() {
        return PaymentCommitRequest.class;
    }

    @Override
    public void execute(PaymentCommitRequest request,
                        Handler<AsyncResult<PaymentCommitResponse>> handler) {
        executeBlocking(future -> {
            val chargingType = request.getChargingType();
            val paymentId = request.getPaymentId();

            try {
                val response = new PaymentCommitResponse();
                response.setChargingType(chargingType);
                val result = bunnyDao.queryPaymentSequence(chargingType, paymentId);
                if (isNull(result)) {
                    response.setRespCode(COMMIT_QUERY_FAILED.respCode());
                    response.setRespDesc(COMMIT_QUERY_FAILED.respDesc());
                    future.complete(response);
                    return;
                }

                val commit = bunnyDao.commitPaymentSequence(chargingType, paymentId);
                if (commit == 1) {
                    response.setRespCode(RESP_CODE_OK);
                    response.setRespDesc(RESP_DESC_SUCCESS);
                    response.setCommit(result.getUsed());
                    response.setUnit(result.getUnit());
                } else {
                    response.setRespCode(COMMIT_FAILED.respCode());
                    response.setRespDesc(COMMIT_FAILED.respDesc());
                }
                future.complete(response);

            } catch (Exception e) {
                log.warn("Payment Commit of chargingType:{} failed:\n", chargingType, e);
                future.fail(e);
            }
        }, handler);
    }
}