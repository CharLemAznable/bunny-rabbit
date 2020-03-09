package com.github.charlemaznable.bunny.rabbit.core.verticle;

import com.github.charlemaznable.bunny.rabbit.config.BunnyConfig;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.core.net.ohclient.OhReq;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.bingoohuang.westid.WestId.next;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_STANDBY;
import static com.github.charlemaznable.bunny.rabbit.core.serve.ServeCallbackConstant.CALLBACK_SUCCESS;
import static com.github.charlemaznable.core.codec.Json.unJson;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static com.github.charlemaznable.core.miner.MinerFactory.getMiner;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public class CallbackVerticle extends AbstractVerticle {

    public static final String CALLBACK_VERTICLE = "BUNNY_CALLBACK_VERTICLE";
    private final CallbackTimerHandler handler;

    public CallbackVerticle(@Nullable BunnyConfig bunnyConfig,
                            @Nullable BunnyCallbackDao bunnyCallbackDao) {
        this.handler = new CallbackTimerHandler(bunnyConfig, bunnyCallbackDao);
    }

    @Override
    public void start() {
        handler.vertx = vertx;
        vertx.setTimer(handler.bunnyConfig.callbackDelay(), handler);
    }

    private static class CallbackTimerHandler implements Handler<Long> {

        private final BunnyConfig bunnyConfig;
        private final BunnyCallbackDao bunnyCallbackDao;
        private Vertx vertx;

        private CallbackTimerHandler(@Nullable BunnyConfig bunnyConfig,
                                     @Nullable BunnyCallbackDao bunnyCallbackDao) {
            this.bunnyConfig = nullThen(bunnyConfig,
                    () -> getMiner(BunnyConfig.class));
            this.bunnyCallbackDao = nullThen(bunnyCallbackDao,
                    () -> getEqler(BunnyCallbackDao.class));
        }

        @Override
        public void handle(Long event) {
            executeBlocking(future -> {
                val callbackRecords = bunnyCallbackDao.queryCallbackRecords();
                for (val callbackRecord : callbackRecords) {
                    val seqId = callbackRecord.getSeqId();
                    val requestContent = callbackRecord.getRequestContent();
                    bunnyCallbackDao.logCallback(toStr(next()),
                            seqId, "callback-req", requestContent);
                    // 回调
                    val callbackResult = new OhReq(callbackRecord.getCallbackUrl())
                            .parameters(unJson(requestContent)).get();
                    // 记录回调响应
                    bunnyCallbackDao.logCallback(toStr(next()),
                            seqId, "callback-rsp", callbackResult);
                    // 更新回调状态
                    bunnyCallbackDao.updateCallbackState(
                            callbackRecord.getChargingType(), seqId,
                            "OK".equals(callbackResult) ? CALLBACK_SUCCESS : CALLBACK_STANDBY);
                }
                future.complete();
            }, asyncResult -> vertx.setTimer(bunnyConfig.callbackDelay(), this));
        }
    }
}
