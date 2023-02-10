package com.github.charlemaznable.bunny.rabbit.core.serve;

import com.github.charlemaznable.bunny.plugin.elf.MtcpElf;
import com.github.charlemaznable.bunny.rabbit.core.common.SwitchPluginLoader;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.CONFIRM_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.PRE_SERVE_FAILED;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public final class ServeService {

    private final SwitchPluginLoader switchPluginLoader;
    private final ChargeCodeMapper chargeCodeMapper;
    private final BunnyServeDao serveDao;
    private final BunnyDao bunnyDao;

    public ServeService(SwitchPluginLoader switchPluginLoader,
                        @Nullable ChargeCodeMapper chargeCodeMapper,
                        @Nullable BunnyServeDao serveDao,
                        @Nullable BunnyDao bunnyDao) {
        this.switchPluginLoader = checkNotNull(switchPluginLoader);
        this.chargeCodeMapper = nullThen(chargeCodeMapper, () -> getConfig(ChargeCodeMapper.class));
        this.serveDao = nullThen(serveDao, () -> getEqler(BunnyServeDao.class));
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
    }

    public void preserve(ServeContext serveContext,
                         Handler<AsyncResult<ServeContext>> handler) {
        try {
            val switchPlugin = switchPluginLoader.load(serveContext.serveName);
            val context = serveContext.context;
            val internalRequest = serveContext.internalRequest;
            switchPlugin.serveDeduct(context, internalRequest, asyncResult -> {
                if (asyncResult.failed()) {
                    handler.handle(failedFuture(asyncResult.cause()));
                    return;
                }

                if (toBoolean(asyncResult.result())) {
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
            val switchPlugin = switchPluginLoader.load(serveContext.serveName);
            val context = serveContext.context;
            val internalRequest = serveContext.internalRequest;
            switchPlugin.serveDeduct(context, internalRequest, asyncResult -> {
                if (asyncResult.failed()) {
                    serveContext.unexpectedThrowable = asyncResult.cause();
                    handler.handle(succeededFuture(serveContext));
                    return;
                }

                if (toBoolean(asyncResult.result())) {
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
        // serveName
        // paymentValue
        // callbackUrl
        // seqId
        // ServeContext出参:
        // 无
        val chargeCode = chargeCodeMapper.chargeCode(serveContext.serveName);
        executeBlocking(serveContext.context, block -> {
            try {
                serveDao.start();

                if (1 != serveDao.updateBalanceByPayment(
                        chargeCode, serveContext.paymentValue)) {
                    // 预扣减失败
                    serveDao.rollback();
                    block.fail(PRE_SERVE_FAILED.exception(
                            "Balance Deduct Failed"));
                    return;
                }

                if (1 != serveDao.createPreserveSequence(
                        chargeCode, serveContext.paymentValue,
                        serveContext.callbackUrl, serveContext.seqId)) {
                    // 生成流水失败
                    serveDao.rollback();
                    block.fail(PRE_SERVE_FAILED.exception(
                            "Sequence Create Failed"));
                    return;
                }

                serveDao.commit();
                block.complete(serveContext);

            } catch (Exception e) {
                serveDao.rollback();
                block.fail(e);
            } finally {
                serveDao.close();
            }
        }, handler);
    }

    private void executeConfirm(ServeContext serveContext,
                                Handler<AsyncResult<ServeContext>> handler) {
        // ServeContext入参:
        // serveName
        // seqId
        // confirmValue
        // ServeContext出参:
        // unexpectedThrowable*
        val chargeCode = chargeCodeMapper.chargeCode(serveContext.serveName);
        Vertx.currentContext().executeBlocking(block -> {
            try {
                MtcpElf.preHandle(serveContext.context);
                serveDao.start();

                if (nonNull(serveDao.queryConfirmedSequence(
                        chargeCode, serveContext.seqId))) {
                    // 流水已确认
                    serveDao.commit();
                    block.complete(serveContext);
                    return;
                }
                if (1 != serveDao.confirmPreserveSequence(
                        chargeCode, serveContext.seqId,
                        nullThen(serveContext.confirmValue, () -> 0))) {
                    // 更新流水失败
                    throw CONFIRM_FAILED.exception("Sequence Confirm Failed");
                }

                if (1 != serveDao.updateBalanceByConfirm(
                        chargeCode, serveContext.seqId)) {
                    // 更新服务余额失败
                    throw CONFIRM_FAILED.exception("Balance Confirm Failed");
                }

                serveDao.commit();
                block.complete(serveContext);

            } catch (Exception e) {
                serveDao.rollback();
                serveContext.unexpectedThrowable = e;
                bunnyDao.logError(toStr(next()), serveContext.seqId,
                        serveContext.unexpectedThrowable.getMessage());
                block.complete(serveContext);
            } finally {
                serveDao.close();
                MtcpElf.afterCompletion();
            }
        }, false, handler);
    }
}
