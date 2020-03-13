package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.plugin.elf.MtcpElf;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CONFIRM_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.PRE_SERVE_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static java.util.Objects.nonNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class ServeService {

    private final BunnyServeDao bunnyServeDao;
    private final BunnyDao bunnyDao;

    @Inject
    @Autowired
    public ServeService(@Nullable BunnyServeDao bunnyServeDao,
                        @Nullable BunnyDao bunnyDao) {
        this.bunnyServeDao = nullThen(bunnyServeDao,
                () -> getEqler(BunnyServeDao.class));
        this.bunnyDao = nullThen(bunnyDao,
                () -> getEqler(BunnyDao.class));
    }

    public void executePreserve(ServeContext serveContext,
                                Handler<AsyncResult<ServeContext>> handler) {
        // ServeContext入参:
        // chargingType
        // paymentValue
        // callbackUrl
        // ServeContext出参:
        // seqId
        executeBlocking(serveContext.context, block -> {
            try {
                bunnyServeDao.start();

                if (1 != bunnyServeDao.updateBalanceByPayment(
                        serveContext.chargingType, serveContext.paymentValue)) {
                    // 预扣减失败
                    bunnyServeDao.rollback();
                    block.fail(PRE_SERVE_FAILED.exception(
                            "Balance Deduct Failed"));
                    return;
                }

                val seqId = toStr(next());
                if (1 != bunnyServeDao.createPreserveSequence(
                        serveContext.chargingType, serveContext.paymentValue,
                        serveContext.callbackUrl, seqId)) {
                    // 生成流水失败
                    bunnyServeDao.rollback();
                    block.fail(PRE_SERVE_FAILED.exception(
                            "Sequence Create Failed"));
                    return;
                }

                bunnyServeDao.commit();
                serveContext.seqId = seqId;
                block.complete(serveContext);

            } catch (Exception e) {
                bunnyServeDao.rollback();
                block.fail(e);
            }
        }, handler);
    }

    public void executeConfirm(ServeContext serveContext,
                               Handler<AsyncResult<ServeContext>> handler) {
        // ServeContext入参:
        // chargingType
        // seqId
        // confirmValue
        // ServeContext出参:
        // unexpectedThrowable*
        Vertx.currentContext().executeBlocking(block -> {
            try {
                MtcpElf.preHandle(serveContext.context);
                bunnyServeDao.start();

                if (nonNull(bunnyServeDao.queryConfirmedSequence(
                        serveContext.chargingType, serveContext.seqId))) {
                    // 流水已确认
                    bunnyServeDao.commit();
                    block.complete(serveContext);
                    return;
                }
                if (1 != bunnyServeDao.confirmPreserveSequence(
                        serveContext.chargingType, serveContext.seqId,
                        nullThen(serveContext.confirmValue, () -> 0))) {
                    // 更新流水失败
                    throw CONFIRM_FAILED.exception("Sequence Confirm Failed");
                }

                if (1 != bunnyServeDao.updateBalanceByConfirm(
                        serveContext.chargingType, serveContext.seqId)) {
                    // 更新服务余额失败
                    throw CONFIRM_FAILED.exception("Balance Confirm Failed");
                }

                bunnyServeDao.commit();
                block.complete(serveContext);

            } catch (Exception e) {
                bunnyServeDao.rollback();
                serveContext.unexpectedThrowable = e;
                bunnyDao.logError(toStr(next()), serveContext.seqId,
                        serveContext.unexpectedThrowable.getMessage());
                block.complete(serveContext);
            } finally {
                MtcpElf.afterCompletion();
            }
        }, false, handler);
    }
}
