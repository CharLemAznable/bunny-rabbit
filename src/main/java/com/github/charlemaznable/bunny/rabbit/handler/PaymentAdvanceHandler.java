package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.bingoohuang.westid.WestId;
import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.PaymentAdvanceRequest;
import com.github.charlemaznable.bunny.client.domain.PaymentAdvanceResponse;
import com.github.charlemaznable.bunny.rabbit.handler.dao.BunnyAdvanceDao;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.ADVANCE_FAILED;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.DEDUCT_FAILED;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Slf4j
@Component
public final class PaymentAdvanceHandler
        implements BunnyHandler<PaymentAdvanceRequest, PaymentAdvanceResponse> {

    @Override
    public String address() {
        return BunnyAddress.PAYMENT_ADVANCE;
    }

    @Override
    public Class<PaymentAdvanceRequest> getRequestClass() {
        return PaymentAdvanceRequest.class;
    }

    @Override
    public void execute(PaymentAdvanceRequest request,
                        Handler<AsyncResult<PaymentAdvanceResponse>> handler) {
        executeBlocking(future -> {
            val response = request.createResponse();
            val chargingType = request.getChargingType();
            val paymentValue = request.getPaymentValue();

            @Cleanup val advanceDao = getEqler(BunnyAdvanceDao.class);
            try {
                advanceDao.start();

                val update = advanceDao.updateBalanceByPayment(
                        chargingType, paymentValue);
                if (update != 1) { // 扣减失败
                    advanceDao.rollback();
                    DEDUCT_FAILED.failed(response);
                    future.complete(response);
                    return;
                }

                val seqId = toStr(WestId.next());
                int create = advanceDao.createPaymentSequence(
                        chargingType, paymentValue, seqId);
                if (create != 1) { // 生成流水失败
                    advanceDao.rollback();
                    ADVANCE_FAILED.failed(response);
                    future.complete(response);
                    return;
                }

                advanceDao.commit();
                response.succeed();
                response.setPaymentId(seqId);
                future.complete(response);

            } catch (Exception e) {
                advanceDao.rollback();
                log.warn("Payment Advance of chargingType:{} failed:\n", chargingType, e);
                future.fail(e);
            }
        }, handler);
    }
}
