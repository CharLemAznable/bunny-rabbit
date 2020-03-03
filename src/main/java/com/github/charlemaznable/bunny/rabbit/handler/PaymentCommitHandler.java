package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.bingoohuang.westid.WestId;
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

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.COMMIT_QUERY_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.toStr;
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
            val response = request.createResponse();
            val chargingType = request.getChargingType();
            val paymentId = request.getPaymentId();

            try {
                val result = bunnyDao.queryPaymentSequence(chargingType, paymentId);
                if (isNull(result)) {
                    COMMIT_QUERY_FAILED.failed(response);
                    future.complete(response);
                    return;
                }

                val commit = bunnyDao.commitPaymentSequence(chargingType, paymentId);
                if (commit == 1) {
                    response.succeed();
                    response.setCommit(result.getUsed());
                    response.setUnit(result.getUnit());
                } else {
                    COMMIT_FAILED.failed(response);
                    bunnyDao.logError(chargingType, paymentId,
                            toStr(WestId.next()), COMMIT_FAILED.respDesc());
                }
                future.complete(response);

            } catch (Exception e) {
                log.warn("Payment Commit of chargingType:{} failed:\n", chargingType, e);
                bunnyDao.logError(chargingType, paymentId,
                        toStr(WestId.next()), e.getMessage());
                future.fail(e);
            }
        }, handler);
    }
}
