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
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class ServeService {

    private final ServeSwitchPluginLoader switchPluginLoader;
    private final BunnyServeDao bunnyServeDao;
    private final BunnyDao bunnyDao;

    @Inject
    @Autowired
    public ServeService(ServeSwitchPluginLoader switchPluginLoader,
                        @Nullable BunnyServeDao bunnyServeDao,
                        @Nullable BunnyDao bunnyDao) {
        this.switchPluginLoader = checkNotNull(switchPluginLoader);
        this.bunnyServeDao = nullThen(bunnyServeDao,
                () -> getEqler(BunnyServeDao.class));
        this.bunnyDao = nullThen(bunnyDao,
                () -> getEqler(BunnyDao.class));
    }

    public void preserve(ServeContext serveContext,
                         Handler<AsyncResult<ServeContext>> handler) {
        try {
            val switchPlugin = switchPluginLoader.load(serveContext.serveType);
            val context = serveContext.context;
            val internalRequest = serveContext.internalRequest;
            switchPlugin.switchDeduct(context, internalRequest, asyncResult -> {
                if (asyncResult.failed()) {
                    handler.handle(failedFuture(asyncResult.cause()));
                    return;
                }

                val switchDeduct = toBoolean(asyncResult.result());
                if (switchDeduct) {
                    // 入库开关打开
                    executePreserve(serveContext, handler);
                } else {
                    handler.handle(succeededFuture(serveContext));
                }
            });
        } catch (Exception e) {
            handler.handle(failedFuture(e));
        }
    }

    public void confirm(ServeContext serveContext,
                        Handler<AsyncResult<ServeContext>> handler) {
        try {
            val switchPlugin = switchPluginLoader.load(serveContext.serveType);
            val context = serveContext.context;
            val internalRequest = serveContext.internalRequest;
            switchPlugin.switchDeduct(context, internalRequest, asyncResult -> {
                if (asyncResult.failed()) {
                    serveContext.unexpectedThrowable = asyncResult.cause();
                    handler.handle(succeededFuture(serveContext));
                    return;
                }

                val switchDeduct = toBoolean(asyncResult.result());
                if (switchDeduct) {
                    // 入库开关打开
                    executeConfirm(serveContext, handler);
                } else {
                    handler.handle(succeededFuture(serveContext));
                }
            });
        } catch (Exception e) {
            serveContext.unexpectedThrowable = e;
            handler.handle(succeededFuture(serveContext));
        }
    }

    private void executePreserve(ServeContext serveContext,
                                 Handler<AsyncResult<ServeContext>> handler) {
        // ServeContext入参:
        // chargingType
        // seqId
        // paymentValue
        // callbackUrl
        // ServeContext出参:
        // 无
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

                if (1 != bunnyServeDao.createPreserveSequence(
                        serveContext.chargingType, serveContext.paymentValue,
                        serveContext.callbackUrl, serveContext.seqId)) {
                    // 生成流水失败
                    bunnyServeDao.rollback();
                    block.fail(PRE_SERVE_FAILED.exception(
                            "Sequence Create Failed"));
                    return;
                }

                bunnyServeDao.commit();
                block.complete(serveContext);

            } catch (Exception e) {
                bunnyServeDao.rollback();
                block.fail(e);
            }
        }, handler);
    }

    private void executeConfirm(ServeContext serveContext,
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
